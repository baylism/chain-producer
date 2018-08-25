package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.Blockchain;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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

@Lazy
@Component
public class ReactiveKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveKafkaProducer.class);

    // @Value("${spring.kafka.bootstrap-servers}")
    // private String bootstrapServers;

    private final SenderOptions<Blockchain, BitcoinBlock> senderOptions;
    private final KafkaSender<Blockchain, BitcoinBlock> sender;


    @Autowired
    public ReactiveKafkaProducer(Map<String, Object> producerProps) {

        logger.info("Building producer with bootstrap ");

        // props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        senderOptions = SenderOptions.create(producerProps);
        sender = KafkaSender.create(senderOptions);

    }


    public Flux<?> send(Flux<BitcoinBlock> source, String topic, Blockchain key) {

        return sender
                .send(
                        source.map(s -> SenderRecord.create(new ProducerRecord<>(topic, key, s), key))
                )
                .doOnError(e -> logger.error("Send failed, terminating.", e))
                // .doOnNext(r -> System.out.println("Message #" + r.correlationMetadata() + " metadata=" + r.recordMetadata()))
                .doOnNext(r -> {
                    Blockchain correlationMetadata = r.correlationMetadata();
                    RecordMetadata metadata = r.recordMetadata();
                    logger.info("Successfully stored block with id {0} and record {1} in Kafka", correlationMetadata, metadata);
                })
                .doOnCancel(sender::close);
    }
}

