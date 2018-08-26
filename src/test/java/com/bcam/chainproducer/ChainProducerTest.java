package com.bcam.chainproducer;

import com.bcam.bcmonitor.model.Blockchain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.Disposable;

import static com.bcam.bcmonitor.model.Blockchain.BITCOIN;

// hostName = "35.229.87.236";
// port = 80;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"HOSTNAME=35.229.87.236", "PORT=80", "spring.kafka.bootstrap-servers=localhost:9092"})
public class ChainProducerTest {

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
    private APIToKafka APIToKafka;


    @Test
    public void main() throws InterruptedException {

        Disposable d = APIToKafka.forwardBlocks(BITCOIN, 1L, 10L);

        Disposable e = APIToKafka.forwardTransactionPool(BITCOIN);

        // Disposable d = APIToKafka.forwardTransactionPool(Blockchain.BITCOIN);



        while (!(d.isDisposed() && e.isDisposed()) ) {
            Thread.sleep(1000L);
        }


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