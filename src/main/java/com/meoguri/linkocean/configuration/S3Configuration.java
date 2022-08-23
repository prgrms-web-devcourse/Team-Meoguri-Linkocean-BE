package com.meoguri.linkocean.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.meoguri.linkocean.infrastructure.s3.S3Properties;
import com.meoguri.linkocean.infrastructure.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class S3Configuration {

	private final S3Properties s3Properties;

	@Bean
	public AmazonS3Client amazonS3Client() {

		return (AmazonS3Client)AmazonS3ClientBuilder.standard()
			.withRegion(s3Properties.getRegion())
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
				s3Properties.getAccessKey(),
				s3Properties.getSecretKey())))
			.build();
	}

	@Bean
	public S3Uploader s3Uploader(AmazonS3Client amazonS3Client) {
		return new S3Uploader(amazonS3Client, s3Properties.getBucket());
	}
}
