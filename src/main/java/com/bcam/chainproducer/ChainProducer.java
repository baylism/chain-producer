package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.bcam.bcmonitor.model.Blockchain.BITCOIN;

@Component
public class ChainProducer implements CommandLineRunner {


    private APIToKafka APIToKafka;


    @Autowired
    public ChainProducer(APIToKafka APIToKafka) {

        this.APIToKafka = APIToKafka;
    }

    @Override
    public void run(String... args) {

        APIToKafka.forwardBlocks(BITCOIN,0L, 100L);
    }



}
