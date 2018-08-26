package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class APIToKafka {

    private RESTClient client;
    private ReactiveKafkaProducer kafkaProducer;


    @Autowired
    public APIToKafka(RESTClient client, ReactiveKafkaProducer kafkaProducer) {

        this.client = client;
        this.kafkaProducer = kafkaProducer;
    }

    // ======= Transactions =======

    public void forwadTransactionsContinuous(Long fromHeight, Long toHeight) {

    }



    // ======= Blocks =======

    // for live data use, https://github.com/spring-projects/spring-data-examples/tree/master/mongodb/change-streams#reactive-style
    public void forwadBlocksContinuous(Long fromHeight, Long toHeight) {

    }


    public Disposable forwardBlocks(Blockchain blockchain, Long fromHeight, Long toHeight) {

        Flux<String> blocksString = client
                .getBlocksFlux(blockchain, fromHeight, toHeight);

        return kafkaProducer.send(blocksString, "blocks", convertChain(blockchain)).subscribe();
    }


    // ======= Pool =======

    public Disposable forwardTransactionPool(Blockchain blockchain) {

        Mono<String> pools = client.getTransactionPool(blockchain);

        return kafkaProducer.send(pools, "pool-tx", "BITCOIN").subscribe();

    }

    public Disposable forwardTransactionPoolContunuous(Blockchain blockchain) {

        Flux<String> pools = Flux.interval(Duration.ofMillis(500))
                .flatMap(x -> client.getTransactionPool(blockchain));

        return kafkaProducer.send(pools, "pool-tx", "BITCOIN").subscribe();

    }

    // ======= Helpers =======

    private String convertChain(Blockchain blockchain) {

        switch (blockchain) {
                case BITCOIN: return "BITCOIN";
            case ZCASH: return "ZCASH";
            case DASH: return "DASH";
            default: throw new RuntimeException("Can't convert blockchain");
        }
    }


}
