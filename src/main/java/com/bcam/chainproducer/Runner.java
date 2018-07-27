package com.bcam.chainproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private RESTPoller poller;

    @Autowired
    public Runner(RESTPoller poller) {
        this.poller = poller;
    }

    @Override
    public void run(String... args) throws Exception {


        for (int i = 2; i > 0; i--) {

            poller.subscribeInfo();

            Thread.sleep(1000L);

        }
    }
}
