// package com.bcam.chainproducer;
//
// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;
// import org.mockserver.integration.ClientAndServer;
// import org.springframework.test.context.TestPropertySource;
//
// import static org.mockserver.integration.ClientAndServer.startClientAndServer;
// import static org.mockserver.model.HttpRequest.request;
// import static org.mockserver.model.HttpResponse.response;
//
// @TestPropertySource(properties = {"HOSTNAME=localhost", "PORT=5000"})
// public class RunnerTest {
//
//     private ClientAndServer mockServer;
//
//     private Runner runner;
//
//     @Before
//     public void startServer() {
//         mockServer = startClientAndServer(5000);
//     }
//
//     @After
//     public void stopServer() {
//         mockServer.stop();
//     }
//
//
//     @Test
//     public void main() {
//
//         mockServer
//                 .when(
//                         request()
//                                 .withMethod("GET")
//                 )
//                 .respond(
//                         response()
//                                 .withBody("returned response!")
//                                 .withHeader("Content-Type", "text/html")
//                 );
//
//         // ChainProducerApplication.main(new String[] {});
//
//
//     }
// }