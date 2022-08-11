package com.meoguri.linkocean.configuration.logger;

import static java.util.Objects.*;

import java.lang.reflect.Method;

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
public class LinkoceanLogAop {

	private static final String NO_USER = "No User";

	/* com.meoguri.linkocean.controller 이하 패키지의 모든 클래스 이하 모든 메서드에 적용 */
	@Pointcut("execution(* com.meoguri.linkocean.controller..*.*(..))")
	private void controller() {
	}

	@Around("controller()")
	public Object loggingUserFlow(ProceedingJoinPoint pjp) throws Throwable {
		/* 메서드 정보 받아오기 */
		Method method = getMethod(pjp);

		/* 요청한 사용자 정보 가져 오기 */
		SecurityUser user = null;
		Object[] args = pjp.getArgs();
		for (Object arg : args) {
			if (arg instanceof SecurityUser) {
				user = (SecurityUser)arg;
			}
		}

		log.info("======= {} request by user {} =======",
			method.getName(),
			nonNull(user) ? user : NO_USER);

		/* 메서드 호출 */
		final Object retVal = pjp.proceed();

		log.info("======= {} response to user {} =======",
			method.getName(),
			nonNull(user) ? user : NO_USER);

		return retVal;
	}

	/* JoinPoint로 메서드 정보 가져오기 */
	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod();
	}
}
