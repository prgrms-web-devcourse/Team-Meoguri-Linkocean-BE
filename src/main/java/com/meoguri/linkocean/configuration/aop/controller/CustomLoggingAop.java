package com.meoguri.linkocean.configuration.aop.controller;

import static java.lang.String.*;
import static java.util.Objects.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class CustomLoggingAop extends BaseControllerAop {

	private static final String ANONYMOUS_USER = "anonymous user";

	@Around("controller()")
	public Object loggingUserFlow(ProceedingJoinPoint joinPoint) throws Throwable {

		final String methodName = getMethodName(joinPoint);
		final SecurityUser user = getSecurityUser(joinPoint);
		final String userString = getUserString(user);

		log.info("[{} request ] by [user {}]", methodName, userString);
		final Object retVal = joinPoint.proceed();
		log.info("[{} response] to [user {}]", methodName, userString);

		return retVal;
	}

	private String getUserString(final SecurityUser user) {
		return nonNull(user) ? format(" id : %d", user.getId()) : ANONYMOUS_USER;
	}
}
