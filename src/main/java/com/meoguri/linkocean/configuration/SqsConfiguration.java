package com.meoguri.linkocean.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.meoguri.linkocean.infrastructure.sqs.SqsProperties;
import com.meoguri.linkocean.infrastructure.sqs.SqsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SqsConfiguration {

	private final SqsProperties sqsProperties;

	@Bean
	public SQSConnectionFactory sqsConnectionFactory() {

		return new SQSConnectionFactory(
			new ProviderConfiguration(),
			AmazonSQSClientBuilder.standard().withRegion(sqsProperties.getRegion())
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
					sqsProperties.getAccessKey(),
					sqsProperties.getSecretKey())))
		);
	}

	@Bean
	public SqsService sqsService(SQSConnectionFactory factory) {

		return new SqsService(factory, sqsProperties.getQueueName());
	}
}
