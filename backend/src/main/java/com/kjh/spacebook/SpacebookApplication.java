package com.kjh.spacebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpacebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpacebookApplication.class, args);
	}

}
