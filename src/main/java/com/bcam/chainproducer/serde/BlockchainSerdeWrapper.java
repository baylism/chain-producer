package com.bcam.chainproducer.serde;


import com.bcam.bcmonitor.model.Blockchain;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BlockchainSerdeWrapper implements Serde<Blockchain> {

    private final Serializer<Blockchain> serializer = new JsonPOJOSerializer<>();
    private final Deserializer<Blockchain> deserializer = new JsonPOJODeserializer<>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<Blockchain> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Blockchain> deserializer() {
        return deserializer;
    }
}