package com.meoguri.linkocean.support.domain.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * 영속성 계층에서 리포지토리를 사용하여 단순한 조회로직을 담당하는 컴포넌트를 'Query' 라고 지정한다.
 * - Query 는 클래스에 적용한다.
 * - Deprecated Since 09.07 - 아키텍처 리팩토링으로인해 사용하지 않게됨
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Deprecated
public @interface Query {

	@AliasFor(annotation = Component.class)
	String value() default "";
}
