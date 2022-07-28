package com.meoguri.linkocean.configuration.querydsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
class QueryDslConfigTest {

	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Test
	void jpqQueryFactory_컨텍스트_등록_성공() {
		assertThat(jpaQueryFactory).isNotNull();
	}
}
