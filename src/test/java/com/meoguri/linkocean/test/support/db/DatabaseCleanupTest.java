package com.meoguri.linkocean.test.support.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.test.support.logging.p6spy.P6spyLogMessageFormatConfiguration;

@SpringBootTest
@Import({P6spyLogMessageFormatConfiguration.class, DatabaseCleanup.class})
class DatabaseCleanupTest {

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@Test
	void 모든_데이터_지우기_성공() {
		databaseCleanup.execute();
	}
}
