package com.bcam.chainproducer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveProducerConfig {

    @Bean
    public Map<String, Object> producerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "chain-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return props;
    }
}
