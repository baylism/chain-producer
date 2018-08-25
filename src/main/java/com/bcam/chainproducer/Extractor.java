package com.bcam.chainproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Extractor {

    private RESTClient client;

    @Autowired
    public Extractor(RESTClient client) {
        this.client = client;
    }


    // public void
}
