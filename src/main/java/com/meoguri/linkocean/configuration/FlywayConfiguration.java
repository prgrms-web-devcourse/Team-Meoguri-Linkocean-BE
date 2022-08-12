package com.meoguri.linkocean.configuration;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

	/* db schema 변경이 발생하는 주석을 푸는것을 고려하세요 */
	@Bean
	public FlywayMigrationStrategy flywayMigrationStrategy() {
		return flyway -> {
			// flyway.clean();
			flyway.repair();
			flyway.migrate();
			flyway.validate();
		};
	}
}
