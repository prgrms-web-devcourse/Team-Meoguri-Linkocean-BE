package com.meoguri.linkocean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableScheduling
@EnableJdbcHttpSession
@SpringBootApplication
public class LinkoceanApplication {

	public static void main(final String[] args) {
		SpringApplication.run(LinkoceanApplication.class, args);
	}

	// @Bean
	public FlywayMigrationStrategy flywayMigrationStrategy() {
		return flyway -> {
			// flyway.clean();
			// flyway.repair();
			// flyway.migrate();
			flyway.validate();
		};
	}
}
