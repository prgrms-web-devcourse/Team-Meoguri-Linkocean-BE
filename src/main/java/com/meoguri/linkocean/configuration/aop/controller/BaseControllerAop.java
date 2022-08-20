package com.meoguri.linkocean.configuration.aop.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;

public abstract class BaseControllerAop {

	@Pointcut("execution(* com.meoguri.linkocean.controller..*.*(..))")
	protected void controller() {
	}

	/* JoinPoint 로 메서드 이름 가져오기 */
	protected String getMethodName(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod().getName();
	}

	/* JoinPoint 로 요청한 사용자 정보 가져 오기 */
	protected SecurityUser getSecurityUser(final ProceedingJoinPoint joinPoint) {
		SecurityUser user = null;
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof SecurityUser) {
				user = (SecurityUser)arg;
			}
		}
		return user;
	}
}
