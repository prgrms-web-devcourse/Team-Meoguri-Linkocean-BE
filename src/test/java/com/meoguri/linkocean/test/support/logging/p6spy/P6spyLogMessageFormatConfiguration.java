package com.meoguri.linkocean.test.support.logging.p6spy;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.boot.test.context.TestConfiguration;

import com.p6spy.engine.spy.P6SpyOptions;

@TestConfiguration
public class P6spyLogMessageFormatConfiguration {

	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(SqlFormatOneline.class.getName());
	}

	public static void pretty() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(SqlFormatPretty.class.getName());
	}

	public static void oneline() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(SqlFormatOneline.class.getName());
	}

	/* target 을 pretty 포맷으로 실행하고 oneline 포맷 으로 변경 */
	public static <T> T pretty(final Supplier<T> supplier, final EntityManager em) {
		em.flush();
		pretty();

		final T result = supplier.get();

		em.flush();
		oneline();

		return result;
	}

}
