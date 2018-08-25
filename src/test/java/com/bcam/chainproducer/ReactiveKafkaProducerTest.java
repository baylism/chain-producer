package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.BitcoinBlock;
import com.bcam.bcmonitor.model.Blockchain;
import com.bcam.bcmonitor.model.TransactionPool;
import com.bcam.chainproducer.kafka.KafkaBlockchainProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class ReactiveKafkaProducerTest {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveKafkaProducerTest.class);

    private static final String TOPIC = "blocks";


    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(
            1,
            false,
            TOPIC);


    @Autowired
    private ReactiveKafkaProducer producer;

    @Autowired
    private Map<String, Object> producerProps;

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());

        logger.info("Beforeclass " + embeddedKafka.getBrokersAsString());
    }

    @Value("${spring.embedded.kafka.brokers}")
    private String brokerAddresses;


    @Test
    public void send() throws Exception {

        String block1 = "{\"hash\":\"00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048\",\"prevBlockHash\":\"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f\",\"height\":1,\"timeStamp\":1231469665,\"sizeBytes\":215,\"difficulty\":1,\"timeReceived\":1535205363,\"txids\":[\"0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098\"],\"medianTime\":0,\"chainWork\":8590065666,\"confirmations\":538401}";

        String block2 = "{\"hash\":\"0000000082b5015589a3fdf2d4baff403e6f0be035a5d9742c1cae6295464449\",\"prevBlockHash\":\"000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd\",\"height\":3,\"timeStamp\":1231470173,\"sizeBytes\":215,\"difficulty\":1,\"timeReceived\":1535205363,\"txids\":[\"999e1c837c76a1b7fbb7e57baf87b309960f5ffefbf2a9b95dd890602272f644\"],\"medianTime\":0,\"chainWork\":17180131332,\"confirmations\":538399}";




        //    create producer and produce

        Flux<String> source = Flux.just(block1, block2);

        producer.rebuildSender(brokerAddresses);
        // producer.rebuildSender();


        Disposable result = producer.send(source, TOPIC, "BITCOIN").subscribe();


        while (! result.isDisposed()) {
            Thread.sleep(1000L);
        }

        // create consumer props
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "testGroup",
                "false",
                embeddedKafka
        );


        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps
        );

        Consumer<String, String> inputConsumer = consumerFactory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(inputConsumer, TOPIC);

        ConsumerRecords<String, String> received = KafkaTestUtils.getRecords(inputConsumer);

        // assertEquals(4, received.count());
        //
        // assertThat(received.iterator().next().value(), equalTo(bitcoinPool1));


        for (ConsumerRecord<String, String> s : received) {
            logger.info("Received on input topic " + TOPIC + " Key: " + s.key() + " Value: " + s.value());
        }

        inputConsumer.close();

        // give stream process time to finish?
        // Thread.sleep(2000L);


    }
}