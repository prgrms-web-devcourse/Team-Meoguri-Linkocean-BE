package com.meoguri.linkocean.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.configuration.querydsl.QueryDslConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({QueryDslConfig.class, P6spyLogMessageFormatConfiguration.class})
@DataJpaTest
public @interface QueryDslTest {
}
