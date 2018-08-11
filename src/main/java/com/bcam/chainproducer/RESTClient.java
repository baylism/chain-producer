package com.bcam.chainproducer;


import com.bcam.bcmonitor.extractor.rpc.ReactiveHTTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
public class RESTClient {

    @Value("${HOSTNAME}")
    private String hostName;

    @Value("${PORT}")
    private int port;

    private ReactiveHTTPClient client;

    public RESTClient() {

    }

    @PostConstruct
    public void buildClient() {
        System.out.println("Creating REST client with hostname " + hostName + " and port " + port);

        client = new ReactiveHTTPClient(hostName, port);
    }

    // public Mono<BitcoinBlock> getBlock() {
    //     return client
    //             .requestResponseSpec("dash", "block", "000000000000003941fb8b64f23b1dc0391892c87dd8054a1f262b70203b2582")
    //             .bodyToMono(BitcoinBlock.class);
    // }

    // public Mono<String> getInfo(String blockchain) {
    //     return client
    //             .requestResponseSpec(blockchain, "blockchaininfo")
    //             .bodyToMono(String.class);
    // }

    // public Mono<String> getInfo(String blockchain) {
    //     return Mono.just("MESSAGE FROM CLIENT");
    // }

}

