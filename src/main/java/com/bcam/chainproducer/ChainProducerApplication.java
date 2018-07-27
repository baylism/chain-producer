package com.bcam.chainproducer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ChainProducerApplication   {



	public static void main(String[] args) {
        new SpringApplicationBuilder(ChainProducerApplication.class).web(WebApplicationType.NONE).run(args);

		// SpringApplication.run(ChainProducerApplication.class, args).web(false).;
	}

}
