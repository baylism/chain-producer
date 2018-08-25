package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.Blockchain;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Component
public class ReactiveKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveKafkaProducer.class);

    private SenderOptions<String, String> senderOptions;
    private KafkaSender<String, String> sender;


    @Autowired
    public ReactiveKafkaProducer(Map<String, Object> producerProps) {

        logger.info("Building producer with bootstrap ");

        // senderOptions = SenderOptions.create(producerProps);
        // sender = KafkaSender.create(senderOptions);

    }

    public void rebuildSender(Map<String, Object> producerProps, String bootstrap) {


        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "chain-producer");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE);
        producerProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);


        senderOptions = SenderOptions.create(producerProps);

        logger.info("Rebuilding producer with bootstrap " + producerProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));


        sender = KafkaSender.create(senderOptions);
    }




    public Flux<?> send(Flux<String> source, String topic, String key) {

        return sender
                .send(
                        source.map(s -> SenderRecord.create(new ProducerRecord<>(topic, key, s), key))
                )
                .doOnError(e -> logger.error("Send failed, terminating.", e))
                // .doOnNext(r -> System.out.println("Message #" + r.correlationMetadata() + " metadata=" + r.recordMetadata()))
                .doOnNext(r -> {
                    String correlationMetadata = r.correlationMetadata();
                    RecordMetadata metadata = r.recordMetadata();
                    logger.info("Successfully stored block with id " + correlationMetadata + " and record "+ metadata + " in Kafka");
                })
                .doOnCancel(sender::close);
    }
}

