package com.meoguri.linkocean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LinkoceanApplication {

	public static void main(final String[] args) {
		SpringApplication.run(LinkoceanApplication.class, args);
	}

	@Bean
	public FlywayMigrationStrategy cleanMigrateStrategy() {
		return flyway -> {
			/*이런 에러 로그가 나오면 아래 주석을 푸세요
			Please remove any half-completed changes then run repair to fix the schema history.*/
			// flyway.repair();

			/* 마이그레이션을 하고 싶으면 아래 주석을 푸세요 */
			// flyway.migrate();
			//
		};
	}
}
