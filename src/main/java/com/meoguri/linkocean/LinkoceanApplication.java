package com.meoguri.linkocean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableScheduling
@EnableJdbcHttpSession
@SpringBootApplication
public class LinkoceanApplication {

	public static void main(final String[] args) {
		SpringApplication.run(LinkoceanApplication.class, args);
	}

}
