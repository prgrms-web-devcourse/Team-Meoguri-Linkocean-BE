package com.meoguri.linkocean.configuration.logger;

import static java.util.Objects.*;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LinkoceanLogAop {

	/* com.meoguri.linkocean 이하 패키지의 모든 클래스 이하 모든 메서드에 적용 */
	@Pointcut("execution(* com.meoguri.linkocean..*.*(..))")
	private void cut() {
	}

	/* Pointcut에 의해 필터링된 경로로 들어오는 경우 메서드 호출 전에 적용 */
	@Before("cut()")
	public void beforeParameterLog(JoinPoint joinPoint) {
		/* 메서드 정보 받아오기 */
		Method method = getMethod(joinPoint);
		log.info("======= call method name = {} =======", method);

		/* 파라미터 받아오기 */
		Object[] args = joinPoint.getArgs();
		if (args.length <= 0)
			log.info("no parameter");
		for (Object arg : args) {
			log.info("parameter type = {}", arg.getClass().getSimpleName());
			log.info("parameter value = {}", arg);
		}
	}

	/* Poincut에 의해 필터링된 경로로 들어오는 경우 메서드 리턴 후에 적용 */
	@AfterReturning(value = "cut()", returning = "returnObj")
	public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
		// 메서드 정보 받아오기
		Method method = getMethod(joinPoint);
		log.info("======= return method name = {} =======", method);

		if (nonNull(returnObj)) {
			log.info("return type = {}", returnObj.getClass().getSimpleName());
			log.info("return value = {}", returnObj);
		}
	}

	/* JoinPoint로 메서드 정보 가져오기 */
	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod();
	}
}
