package com.meoguri.linkocean.test.support.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.test.support.db.DatabaseCleanup;
import com.meoguri.linkocean.test.support.logging.p6spy.P6spyLogMessageFormatConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Tag("controller")
@AutoConfigureMockMvc
@SpringBootTest
@Import({P6spyLogMessageFormatConfiguration.class, DatabaseCleanup.class})
public @interface ControllerTest {
}
