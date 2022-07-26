package com.meoguri.linkocean.domain.linkmetadata.scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;

@SpringBootTest(webEnvironment = NONE)
class LinkMetadataSynchronizeSchedulerTest {

	@Autowired
	private ScheduledTaskHolder scheduledTaskHolder;

	@Test
	void 링크_메타_데이터_동기화_스케줄러_등록_성공() {
		//when
		final long count = scheduledTaskHolder.getScheduledTasks().stream()
			.filter(scheduledTask -> scheduledTask.getTask() instanceof CronTask)
			.map(scheduledTask -> (CronTask)scheduledTask.getTask())
			.filter(this::isMyScheduler)
			.count();

		//then
		assertThat(count).isEqualTo(1L);
	}

	private boolean isMyScheduler(CronTask cronTask) {
		return cronTask.getExpression().equals("0 0 0 * * MON")
			&& cronTask.toString().equals(
			"com.meoguri.linkocean.domain.linkmetadata.scheduler.LinkMetadataSynchronizeScheduler.synchronizeAllData");
	}
}
