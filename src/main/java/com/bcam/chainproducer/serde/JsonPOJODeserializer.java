package com.bcam.chainproducer.serde;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class JsonPOJODeserializer<T> implements Deserializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonPOJOSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private Class<T> tClass;

    /**
     * Default constructor needed by Kafka
     */
    public JsonPOJODeserializer() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        tClass = (Class<T>) props.get("JsonPOJOClass");
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        T data;
        try {
            // logger.info("DESER Topic: " + topic + " Bytes: " + bytes);
            // logger.info("CLASS: " + tClass);
            data = objectMapper.readValue(bytes, tClass);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return data;
    }

    @Override
    public void close() {

    }
}
