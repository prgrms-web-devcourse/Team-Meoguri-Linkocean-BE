package com.meoguri.linkocean.support.domain.persistence.aop;

import static com.meoguri.linkocean.exception.Preconditions.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FindByIdNullToLinkoceanRuntimeExceptionAop {

	@Around("@within(com.meoguri.linkocean.support.domain.persistence.aop.RequireSingleResult)")
	public Object nullToLinkoceanRuntimeException(final ProceedingJoinPoint joinPoint) throws Throwable {

		final Object entity = joinPoint.proceed();
		checkCondition(entity != null, "no such %s id :%d", getEntityName(joinPoint), getId(joinPoint));

		return entity;
	}

	private String getEntityName(final ProceedingJoinPoint joinPoint) {
		final Signature signature = joinPoint.getSignature();
		return ((MethodSignature)signature).getReturnType().getSimpleName().toLowerCase();
	}

	private long getId(final ProceedingJoinPoint joinPoint) {
		return (long)joinPoint.getArgs()[0];
	}

}
