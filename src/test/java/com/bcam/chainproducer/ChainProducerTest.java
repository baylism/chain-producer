package com.bcam.chainproducer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.bcam.bcmonitor.model.Blockchain.BITCOIN;
import static com.bcam.bcmonitor.model.Blockchain.DASH;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"HOSTNAME=35.229.87.236", "PORT=80", "KAFKA_BOOTSTRAP_SERVERS=localhost:9092"})
public class ChainProducerTest {


    @Autowired
    private APIToKafka APIToKafka;


    @Test
    public void main() {

        // APIToKafka
        //         .forwardBlocks(BITCOIN, 1L, 10L)
        //         .blockLast();

        // APIToKafka
        //         .forwardBlocks(BITCOIN, 500000L, 500002L)
        //         .blockLast();
        //
        APIToKafka
                .forwardBlocks(DASH, 934423L).blockLast();

        // APIToKafka
        //         .forwardTransactionPoolContunuous(BITCOIN, 4000L)
        //         .blockLast();

    }
}