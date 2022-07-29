package com.meoguri.linkocean.controller;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;

@AutoConfigureMockMvc
@SpringBootTest
@Import(P6spyLogMessageFormatConfiguration.class)
public class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected String createJson(Object dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	protected static String getBaseUrl(final Class<?> clazz) {

		return Stream.of(Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, RequestMapping.class))
				.map(RequestMapping::value)
				.orElseThrow(NullPointerException::new))
			.findFirst()
			.orElseThrow(NullPointerException::new);
	}
}
