package com.meoguri.linkocean.infrastructure.sqs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "cloud.aws.sqs")
public class SqsProperties {

	private String accessKey;
	private String secretKey;
	private String region;
	private String queueName;
}
