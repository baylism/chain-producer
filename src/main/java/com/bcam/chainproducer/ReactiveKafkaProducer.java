package com.bcam.chainproducer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Component
public class ReactiveKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveKafkaProducer.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final Map<String, Object> props;
    private final SenderOptions<String, String> senderOptions;
    private final KafkaSender<String, String> sender;


    @Autowired
    public ReactiveKafkaProducer(Map<String, Object> producerProps) {

        logger.info("Building producer with bootstrap " + bootstrapServers);

        props = producerProps;


        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);

    }

    // private Map<String, Object> buildProps(String bootstrapServers) {
    //
    //     Map<String, Object> props = new HashMap<>();
    //
    //     props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    //     props.put(ProducerConfig.CLIENT_ID_CONFIG, "chain-producer");
    //     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //     props.put(ProducerConfig.ACKS_CONFIG, "all");
    //     props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE);
    //     props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    //
    //     return props;
    // }

    public Flux<?> send(Flux<String> source, String topic, String key) {

        return sender
                .send(
                        source.map(s -> SenderRecord.create(new ProducerRecord<>(topic, key, s), key))
                )
                .doOnError(e -> logger.error("Send failed, terminating.", e))
                .doOnNext(r -> System.out.println("Message #" + r.correlationMetadata() + " metadata=" + r.recordMetadata()))
                .doOnNext(r -> {
                    String id = r.correlationMetadata();
                    logger.info("Successfully stored person with id {} in Kafka", id);
                })
                .doOnCancel(sender::close);
    }
}

