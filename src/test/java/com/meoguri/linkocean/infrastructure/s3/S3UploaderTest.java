package com.meoguri.linkocean.infrastructure.s3;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class S3UploaderTest {

	@Autowired
	private S3Uploader awsS3Uploader;

	@Test
	void S3uploader_업로드_성공() throws IOException {
		// given
		String path = "test.png";
		String contentType = "image/png";
		String dirName = "test";

		MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());

		// when
		String urlPath = awsS3Uploader.upload(file, dirName);

		// then
		assertThat(urlPath).contains(path);
		assertThat(urlPath).contains(dirName);
	}
}