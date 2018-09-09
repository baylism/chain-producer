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
    private ReactiveKafkaProducerTyped kafkaProducer;


    @Autowired
    public APIToKafka(RESTClient client, ReactiveKafkaProducerTyped kafkaProducer) {

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
        enablePool.put(DASH, Boolean.TRUE);
        enablePool.put(ZCASH, Boolean.FALSE);

    }


    // ============================ Blocks ============================

    @Scheduled(fixedDelay = 5000L)
    public void updateChainTips() {

        tips.forEachKey(Long.MAX_VALUE, this::updateTip);

    }

    private void updateTip(Blockchain blockchain) {

        if (! enableTracking.get(blockchain)) {
            return;
        }

        try {
            Long newTip = client.getBestHeight(blockchain).block();
            Long oldTip = tips.get(blockchain);

            // first time run for this blockchain. Just save the current tip.
            if (oldTip == -1) {
                tips.put(blockchain, newTip);
                return;
            }

            // if tip has progressed, send new blocks to kafka
            if (newTip > oldTip) {

                forwardBlocks(blockchain, oldTip + 1).blockLast();

                logger.info("Updated kafka blocks for " + blockchain + " to height " + newTip);

                tips.put(blockchain, newTip);
                logger.info("Updated tip for " + blockchain + " to height " + newTip);

            }

        } catch (Exception e){

            logger.info("Couldn't get tip " + e);
        }

    }


    // one-off
    public Flux<?> forwardBlocks(Blockchain blockchain, Long fromHeight, Long toHeight) {

        Flux<String> blocksString = client
                .getBlocks(blockchain, fromHeight, toHeight);

        return kafkaProducer.send(blocksString, "blocks", blockchain);
    }

    // one-off
    public Flux<?> forwardBlocks(Blockchain blockchain, Long fromHeight) {

        Flux<String> blocksString = client
                .getBlocks(blockchain, fromHeight);

        return kafkaProducer.send(blocksString, "blocks", blockchain);
    }


    // ============================ Pool ============================

    @Scheduled(fixedDelay = 5000L)
    public void updatePool() {

        tips.forEachKey(Long.MAX_VALUE, this::forwardTransactionPool);

    }


    private void forwardTransactionPool(Blockchain blockchain) {

        if (! enablePool.get(blockchain)) {
            return;
        }

        Mono<String> pools = client.getTransactionPool(blockchain);

        kafkaProducer.send(pools, "pool-tx", blockchain).blockLast();

    }


    // one-off
    public Flux<?> forwardTransactionPoolContunuous(Blockchain blockchain, Long intervalMillis) {

        Flux<String> pools = Flux.interval(Duration.ofMillis(intervalMillis))
                .flatMap(x -> client.getTransactionPool(blockchain));

        return kafkaProducer.send(pools, "pool-tx", blockchain);

    }


    // ============================ Helpers ============================
    //
    // private String convertChain(Blockchain blockchain) {
    //
    //     switch (blockchain) {
    //             case BITCOIN: return "BITCOIN";
    //         case ZCASH: return "ZCASH";
    //         case DASH: return "DASH";
    //         default: throw new RuntimeException("Can't convert blockchain");
    //     }
    // }


}
