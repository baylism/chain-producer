package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.Blockchain;
import com.bcam.bcmonitor.model.TransactionPool;
import com.bcam.chainproducer.serde.BlockchainSerdeWrapper;
import com.bcam.chainproducer.serde.JsonPOJODeserializer;
import com.bcam.chainproducer.serde.JsonPOJOSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveProducerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveProducerConfig.class);


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerProps() {

        logger.info("Bootstrap is " + bootstrapServers);

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "chain-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return props;
    }


    @Bean
    public Serde<BitcoinBlock> blockSerde() {

        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("JsonPOJOClass", BitcoinBlock.class);

        Serializer<BitcoinBlock> blockSerializer = new JsonPOJOSerializer<>();
        Deserializer<BitcoinBlock> blockDeserializer = new JsonPOJODeserializer<>();

        blockSerializer.configure(serdeProps, false);
        blockDeserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(blockSerializer, blockDeserializer);
    }


    @Bean
    public Serde<Blockchain> blockchainSerde(BlockchainSerdeWrapper blockchainSerde) {

        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("JsonPOJOClass", Blockchain.class);
        blockchainSerde.configure(serdeProps, true);

        return blockchainSerde;

    }
}