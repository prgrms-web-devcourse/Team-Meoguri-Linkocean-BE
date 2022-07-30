package com.meoguri.linkocean.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * 영속성 계층에서 리포지토리를 사용하여 단순한 조회로직을 담당하는 컴포넌트를 'Query' 라고 지정한다.
 *
 * Query 는 서비스에서 타겟 엔티티의 연관관계 대상 엔티티를 조회 하는 용도 등 사용한다.
 * Query 는 재사용성을 고려하여 도입한다.
 * Query 는 클래스에 적용한다.
 * 도메인별 다양한 Query 가 등장하면 interface 도입을 고려한다.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Query {

	@AliasFor(annotation = Component.class)
	String value() default "";
}
