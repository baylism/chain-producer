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

@Component
public class RESTClient {

    // @TestPropertySource(properties = {"HOSTNAME=localhost", "PORT=9998"})

    // @Value("${HOSTNAME}")
    private String hostName;

    // @Value("${PORT}")
    private int port;

    private ReactiveHTTPClient client;

    public RESTClient() {

    }

    @PostConstruct
    public void buildClient() {
        System.out.println("Creating REST client with hostname " + hostName + " and port " + port);

        client = new ReactiveHTTPClient(hostName, port);
    }

    public Flux<BitcoinBlock> getBlocksProducer(Blockchain blockchain, Long fromHeight, Long toHeight) {
        return client
                .getResponseSpec(convertChain(blockchain), "blocks", fromHeight.toString(), toHeight.toString())
                .bodyToFlux(BitcoinBlock.class);
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

