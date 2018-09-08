package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bcam.bcmonitor.model.Blockchain.*;

@Component
public class APIToKafka {

    private static final Logger logger = LoggerFactory.getLogger(APIToKafka.class);

    private ConcurrentHashMap<Blockchain, Long> tips;
    private Map<Blockchain, Boolean> enableTracking;
    private Map<Blockchain, Boolean> enablePool;


    private RESTClient client;
    private ReactiveKafkaProducer kafkaProducer;


    @Autowired
    public APIToKafka(RESTClient client, ReactiveKafkaProducer kafkaProducer) {

        this.client = client;
        this.kafkaProducer = kafkaProducer;

        tips = new ConcurrentHashMap<>();
        tips.put(BITCOIN, -1L);
        tips.put(DASH, -1L);
        tips.put(ZCASH, -1L);

        enableTracking = new HashMap<>();
        enableTracking.put(BITCOIN, Boolean.FALSE);
        enableTracking.put(DASH, Boolean.TRUE);
        enableTracking.put(ZCASH, Boolean.FALSE);

        enablePool = new HashMap<>();
        enablePool.put(BITCOIN, Boolean.FALSE);
        enablePool.put(DASH, Boolean.FALSE);
        enablePool.put(ZCASH, Boolean.FALSE);

    }


    // ============================ Blocks ============================

    @Scheduled(fixedDelay = 5000L)
    public void updateChainTips() {

        tips.forEachKey(Long.MAX_VALUE, this::updateTip);

    }

    private void updateTip(Blockchain blockchain) {

        if (!enableTracking.get(blockchain)) {
            return;
        }

        try {
            Long newTip = client.getBestHeight(blockchain).block();
            Long oldTip = tips.get(blockchain);

            // if tip has progressed, send new blocks to kafka
            if (newTip > oldTip) {

                Flux<String> newBlock = client.getBlocksLatest(blockchain, oldTip + 1);
                kafkaProducer.send(newBlock, "blocks", convertChain(blockchain));

                tips.put(blockchain, newTip);
                logger.info("Updated kafka blocks for " + blockchain + " to height " + newTip);
            }

        } catch (Exception e){

            logger.info("Couldn't get tip " + e);
        }

    }


    // one-off
    public Flux<?> forwardBlocks(Blockchain blockchain, Long fromHeight, Long toHeight) {

        Flux<String> blocksString = client
                .getBlocksFlux(blockchain, fromHeight, toHeight);

        return kafkaProducer.send(blocksString, "blocks", convertChain(blockchain));
    }


    // ============================ Pool ============================

    @Scheduled(fixedDelay = 5000L)
    public void updatePool() {

        tips.forEachKey(Long.MAX_VALUE, this::forwardTransactionPool);

    }


    private void forwardTransactionPool(Blockchain blockchain) {

        if (!enablePool.get(blockchain)) {
            return;
        }

        Mono<String> pools = client.getTransactionPool(blockchain);

        kafkaProducer.send(pools, "pool-tx", convertChain(blockchain));

    }


    // one-off
    public Flux<?> forwardTransactionPoolContunuous(Blockchain blockchain, Long intervalMillis) {

        Flux<String> pools = Flux.interval(Duration.ofMillis(intervalMillis))
                .flatMap(x -> client.getTransactionPool(blockchain));

        return kafkaProducer.send(pools, "pool-tx", "BITCOIN");

    }


    // ============================ Helpers ============================

    private String convertChain(Blockchain blockchain) {

        switch (blockchain) {
                case BITCOIN: return "BITCOIN";
            case ZCASH: return "ZCASH";
            case DASH: return "DASH";
            default: throw new RuntimeException("Can't convert blockchain");
        }
    }


}
