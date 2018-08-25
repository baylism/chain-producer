package com.bcam.chainproducer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

// hostName = "35.229.87.236";
// port = 80;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication
@TestPropertySource(properties = {"HOSTNAME=35.229.87.236", "PORT=80", "spring.kafka.bootstrap-servers=localhost:3333"})
public class RunnerTest {

    // private ClientAndServer mockServer;
    //
    // private Runner runner;
    //
    // @Before
    // public void startServer() {
    //     mockServer = startClientAndServer(5000);
    // }
    //
    // @After
    // public void stopServer() {
    //     mockServer.stop();
    // }

    @Autowired
    private Extractor extractor;


    @Test
    public void main() {

        extractor.forwardBitcoinBlocks(1L, 100L);
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

        // ChainProducerApplication.main(new String[] {});


    }
}