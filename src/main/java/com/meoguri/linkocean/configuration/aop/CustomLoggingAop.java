package com.meoguri.linkocean.configuration.aop;

import static java.util.Objects.*;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class CustomLoggingAop {

	private static final String ANONYMOUS_USER = "anonymous";

	/* 컨트롤러의 모든 메서드에 적용 */
	@Pointcut("execution(* com.meoguri.linkocean.controller..*.*(..))")
	private void controller() {
	}

	@Around("controller()")
	public Object loggingUserFlow(ProceedingJoinPoint joinPoint) throws Throwable {

		final String methodName = getMethodName(joinPoint);
		final String user = getSecurityUser(joinPoint);

		log.info("======= {} request by user {} =======", methodName, user);
		final Object retVal = joinPoint.proceed();
		log.info("======= {} response to user {} =======", methodName, user);

		return retVal;
	}

	/* JoinPoint 로 메서드 이름 가져오기 */
	private String getMethodName(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod().getName();
	}

	/* JoinPoint 로 요청한 사용자 정보 가져 오기 */
	private String getSecurityUser(final ProceedingJoinPoint joinPoint) {
		SecurityUser user = null;
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof SecurityUser) {
				user = (SecurityUser)arg;
			}
		}
		return nonNull(user) ? user.toString() : ANONYMOUS_USER;
	}
}
