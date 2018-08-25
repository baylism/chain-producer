package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class Extractor {

    private RESTClient client;
    private ReactiveKafkaProducer kafkaProducer;
    private ObjectMapper mapper;


    @Autowired
    public Extractor(RESTClient client, ReactiveKafkaProducer kafkaProducer) {
        this.client = client;
        this.kafkaProducer = kafkaProducer;
        this.mapper = new ObjectMapper();
    }


    public void forwardBitcoinBlocks(Long fromHeight, Long toHeight) {

        Flux<String> blocksString = client
                .getBlocksFlux(Blockchain.BITCOIN, fromHeight, toHeight)
                .map(block -> {
                    try {
                        return mapper.writeValueAsString(block);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });

        kafkaProducer.send(blocksString, "blocks", "BITCOIN").subscribe();

    }

}
