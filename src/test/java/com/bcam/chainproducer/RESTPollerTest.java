package com.bcam.chainproducer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestPropertySource(properties = {"HOSTNAME=localhost", "PORT=6789"})
@SpringBootTest
@RunWith(SpringRunner.class)
public class RESTPollerTest {

    private ClientAndServer mockServer;

    // @Autowired
    // private RESTPoller poller;
    //
    //
    // @Before
    // public void startServer() {
    //     mockServer = startClientAndServer(6789);
    // }
    //
    // @After
    // public void stopServer() {
    //     mockServer.stop();
    // }
    //
    //
    // @Test
    // public void subscribeInfo() {
    //     // mockServer = startClientAndServer(6789);
    //
    //
    //     mockServer
    //             .when(
    //                     request()
    //                             .withMethod("GET")
    //             )
    //             .respond(
    //                     response()
    //                             .withBody("returned response!")
    //                             .withHeader("Content-Type", "text/html")
    //             );
    //
    //     // poller.subscribeInfo();
    // }

}