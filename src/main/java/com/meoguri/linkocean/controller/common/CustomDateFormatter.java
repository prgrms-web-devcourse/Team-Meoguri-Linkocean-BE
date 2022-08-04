package com.meoguri.linkocean.controller.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@JsonComponent
public class CustomDateFormatter {

	@Value("${spring.jackson.date-format:yyyy-MM-dd}")
	private String pattern;

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder() {
		return builder -> {
			TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
			DateFormat df = new SimpleDateFormat(pattern);
			df.setTimeZone(tz);
			builder.failOnEmptyBeans(false)
				.failOnUnknownProperties(false)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.dateFormat(df);
		};
	}

	@Bean
	public LocalDateTimeSerializer localDateTimeDeserializer() {
		return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(
		LocalDateTimeSerializer localDateTimeDeserializer) {
		return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer);
	}
}
