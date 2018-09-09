package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BlockchainSerializer implements Serializer<Blockchain> {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Blockchain data) {

        try {
            logger.info("SER Topic: " + topic + " Bytes: " + data);
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }

    }


    @Override
    public void close() {

    }
}
