package com.bcam.chainproducer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.bcam.bcmonitor.model.Blockchain.BITCOIN;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"HOSTNAME=35.229.87.236", "PORT=80", "spring.kafka.bootstrap-servers=localhost:9092"})
public class ChainProducerTest {


    @Autowired
    private APIToKafka APIToKafka;


    @Test
    public void main() {

        APIToKafka
                .forwardBlocks(BITCOIN, 1L, 10L)
                .blockLast();

        APIToKafka
                .forwardTransactionPoolContunuous(BITCOIN)
                .blockLast();


    }
}