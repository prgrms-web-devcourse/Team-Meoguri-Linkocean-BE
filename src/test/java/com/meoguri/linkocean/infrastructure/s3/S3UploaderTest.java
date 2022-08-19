package com.meoguri.linkocean.infrastructure.s3;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class S3UploaderTest {

	private static final String S3_SERVICE_END_POINT = "https://meguri-linkocean.s3.ap-northeast-2.amazonaws.com";
	@Autowired
	private S3Uploader s3Uploader;


	@Test
	void S3uploader_업로드_성공() {
		// given
		MockMultipartFile file = new MockMultipartFile("test-file", "test.png", "image/png", "test".getBytes());

		// when
		String imageUrl = s3Uploader.upload(file, "test");

		// then
		assertThat(imageUrl).startsWith(String.join("/", S3_SERVICE_END_POINT, "test"));
		assertThat(imageUrl).endsWith(".png");
	}

	@Test
	void S3uploader_업로드_실패_유효하지_않은_파일_형식() {
		// given
		MockMultipartFile file = new MockMultipartFile("test-file", "test.bmp", "image/bmp", "test".getBytes());

		// when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> s3Uploader.upload(file, "test"));
	}
}
