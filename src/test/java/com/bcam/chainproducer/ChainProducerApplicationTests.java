package com.bcam.chainproducer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"HOSTNAME=35.229.87.236", "PORT=80", "KAFKA_BOOTSTRAP_SERVERS=localhost:9092"})
public class ChainProducerApplicationTests {

	@Test
	public void contextLoads() {

        // ClientAndServer mockServer = startClientAndServer(5000);
        //
        // mockServer
        //         .when(
        //                 request()
        //                         .withMethod("GET")
        //         )
        //         .respond(
        //                 response()
        //                         .withBody("returned response!")
        //                         .withHeader("Content-Type", "text/html")
        //         );

	}

}
