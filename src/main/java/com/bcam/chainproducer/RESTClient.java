package com.bcam.chainproducer;


import com.bcam.bcmonitor.extractor.rpc.ReactiveHTTPClient;
import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.Blockchain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static com.bcam.bcmonitor.model.Blockchain.BITCOIN;
import static com.bcam.bcmonitor.model.Blockchain.DASH;
import static com.bcam.bcmonitor.model.Blockchain.ZCASH;


/**
 *
 * for live data use, https://github.com/spring-projects/spring-data-examples/tree/master/mongodb/change-streams#reactive-style
 *
 *
 */
@Component
public class RESTClient {

    // @TestPropertySource(properties = {"HOSTNAME=localhost", "PORT=9998"})

    @Value("${HOSTNAME}")
    private String hostName;

    @Value("${PORT}")
    private int port;

    private ReactiveHTTPClient client;

    public RESTClient() {

        // client = new ReactiveHTTPClient(hostName, port);

        // hostName = "35.229.87.236";
        // port = 80;

    }

    @PostConstruct
    public void buildClient() {
        System.out.println("Creating REST client with hostname " + hostName + " and port " + port);

        client = new ReactiveHTTPClient(hostName, port);
    }


    /**
     * open a stream of blocks from fromheight to tohieght
     */
    public Flux<String> getBlocksFlux(Blockchain blockchain, Long fromHeight, Long toHeight) {

        return client
                .getResponseSpec(convertChain(blockchain), "blocks", fromHeight.toString(), toHeight.toString())
                .bodyToFlux(String.class);

    }


    /**
     * open a stream of blocks form fromheight to the latest known height
     */
    public Flux<String> getBlocksFlux(Blockchain blockchain, Long fromHeight) {

        return client
                .getResponseSpec(convertChain(blockchain), "blocks", fromHeight.toString())
                .bodyToFlux(String.class);

    }


    /**
     * get the current state of the transactino pool
     */
    public Mono<String> getTransactionPool(Blockchain blockchain) {

        return client
                .getResponseSpec(convertChain(blockchain), "transactionpool")
                .bodyToMono(String.class);

    }


    private String convertChain(Blockchain blockchain) {

        switch (blockchain) {
            case BITCOIN: return "bitcoin";
            case ZCASH: return "zcash";
            case DASH: return "dash";
            default: throw new RuntimeException("Can't convert blockchain");
        }
    }

}

