package com.bcam.chainproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {


    private Extractor extractor;


    @Autowired
    public Runner(Extractor extractor) {

        this.extractor = extractor;
    }

    @Override
    public void run(String... args) {

        extractor.forwardBitcoinBlocks(0L, 100L);
    }



}
