package com.meoguri.linkocean.configuration.security.oauth;

import static org.springframework.http.HttpStatus.*;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
		final AuthenticationException ex) throws IOException, ServletException {
		log.debug(ex.getMessage(), ex);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(BAD_REQUEST.value());

		try (OutputStream os = response.getOutputStream()) {
			final ObjectMapper objectMapper = new ObjectMapper();
			final ObjectNode errorResponse = objectMapper.createObjectNode()
				.put("code", BAD_REQUEST.value())
				.put("message", "페이지에 접근할 권한이 없습니다.");
			objectMapper.writeValue(os, errorResponse);
			os.flush();
		}
	}
}
