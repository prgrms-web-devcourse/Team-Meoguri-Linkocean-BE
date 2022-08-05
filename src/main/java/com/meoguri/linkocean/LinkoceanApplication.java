package com.meoguri.linkocean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "com.meoguri.linkocean.configuration.security.jwt")
@SpringBootApplication
public class LinkoceanApplication {

	public static void main(final String[] args) {
		SpringApplication.run(LinkoceanApplication.class, args);
	}

}
