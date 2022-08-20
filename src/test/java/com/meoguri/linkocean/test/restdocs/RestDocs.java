package com.meoguri.linkocean.test.restdocs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@DisabledOnOs(value = {OS.MAC, OS.WINDOWS}, disabledReason = "테스트 속도를 위해 Disable")
public @interface RestDocs {
}
