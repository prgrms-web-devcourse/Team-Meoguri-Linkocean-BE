package com.meoguri.linkocean.test.support.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("restdocs")
@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public @interface RestDocsTest {
}
