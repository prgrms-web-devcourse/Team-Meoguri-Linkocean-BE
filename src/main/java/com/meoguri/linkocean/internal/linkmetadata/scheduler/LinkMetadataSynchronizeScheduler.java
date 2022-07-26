package com.meoguri.linkocean.internal.linkmetadata.scheduler;

import static java.util.Objects.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.meoguri.linkocean.internal.linkmetadata.service.LinkMetadataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 링크 메타 데이터를 주기적으로 동기화 시켜주는 스케줄러
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LinkMetadataSynchronizeScheduler {

	public static final int BATCH_SIZE = 100;

	private final LinkMetadataService linkMetadataService;

	/* 매주 월요일 0시 0분 0초에 모든 링크 메타 데이터 동기화 */
	@Scheduled(cron = "0 0 0 * * MON")
	public void synchronizeAllData() {
		log.info("start synchronize all linkmetadata");

		Pageable pageable = PageRequest.of(0, BATCH_SIZE);
		do {
			pageable = linkMetadataService.synchronizeDataAndReturnNextPageable(pageable);
		} while (nonNull(pageable));

		log.info("finish synchronize all linkmetadata");
	}
}
