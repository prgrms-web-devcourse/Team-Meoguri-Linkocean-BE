package com.meoguri.linkocean.configuration.aop.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.meoguri.linkocean.domain.notification.entity.GetBookmarkRecord;
import com.meoguri.linkocean.domain.notification.persistence.GetBookmarkRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GetDetailedBookmarkAop {

	private final GetBookmarkRecordRepository repository;

	@Around("execution(* com.meoguri.linkocean.domain.bookmark.service.BookmarkService.getDetailedBookmark(..))")
	public Object saveGetBookmarkRecord(ProceedingJoinPoint joinPoint) throws Throwable {

		final Object[] args = joinPoint.getArgs();

		final long profileId = (long)args[0];
		final long bookmarkId = (long)args[1];

		final Object retVal = joinPoint.proceed();

		repository.findByProfileIdAndBookmarkId(profileId, bookmarkId)
			.ifPresentOrElse(
				GetBookmarkRecord::updateGetAt,
				() -> repository.save(new GetBookmarkRecord(profileId, bookmarkId))
			);
		return retVal;
	}

}
