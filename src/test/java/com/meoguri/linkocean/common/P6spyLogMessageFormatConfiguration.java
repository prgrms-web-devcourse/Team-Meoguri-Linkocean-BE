package com.meoguri.linkocean.common;

import javax.annotation.PostConstruct;

import org.springframework.boot.test.context.TestConfiguration;

import com.p6spy.engine.spy.P6SpyOptions;

@TestConfiguration
public class P6spyLogMessageFormatConfiguration {

	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomP6spySqlFormat.class.getName());
	}

}
