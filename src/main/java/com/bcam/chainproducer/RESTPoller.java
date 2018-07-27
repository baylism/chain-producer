package com.bcam.chainproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RESTPoller {

    private KafkaBlockchainProducer producer;
    private RESTClient client;
    private ArrayList<String> blockchains;


    @Autowired
    public RESTPoller(KafkaBlockchainProducer producer, RESTClient client) {
        this.producer = producer;
        this.client = client;

        blockchains = new ArrayList<>();
        blockchains.add("dash");
    }

    public void subscribeInfo() {

        for (String chain : blockchains) {
            client
                    .getInfo(chain)
                    .subscribe(info -> producer.sendData("info", "newInfo", info));
        }

    }

}
