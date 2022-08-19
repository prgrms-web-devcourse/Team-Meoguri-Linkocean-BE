package com.meoguri.linkocean.infrastructure.s3;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class S3Uploader {

	private static final List<String> EXTENSIONS_IMAGE = List.of("jpg", "png", "jpeg");

	private final AmazonS3Client amazonS3Client;

	private final String bucket;

	/**
	 * multipartFile 을 S3 버킷의 directory 에 저장한다.
	 * 주어진 multipartFile 이 저장된 imageUrl 을 반환한다.
	 * 주어진 multipartFile 이 null 이라면 null 을 반환한다.
	 */
	public String upload(final MultipartFile multipartFile, final String dirName) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}

		final String saveFilePath = getSaveFilePath(multipartFile.getOriginalFilename(), dirName);
		final InputStream input = getInputStream(multipartFile);
		final ObjectMetadata metadata = new ObjectMetadata();

		return uploadInternal(saveFilePath, input, metadata);
	}

	private String uploadInternal(final String saveFilePath, final InputStream input, final ObjectMetadata metadata) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, saveFilePath, input, metadata)
			.withCannedAcl(CannedAccessControlList.PublicRead));

		return amazonS3Client.getUrl(bucket, saveFilePath).toString();
	}

	private String getSaveFilePath(final String originalFileName, final String dirName) {
		final String extension = FilenameUtils.getExtension(originalFileName);
		checkArgument(EXTENSIONS_IMAGE.contains(extension), "유효하지 않은 파일 형식입니다.");

		final String saveFilename = String.join(".", UUID.randomUUID().toString(), extension);
		return String.join("/", dirName, saveFilename);
	}

	private InputStream getInputStream(final MultipartFile multipartFile) {
		try {
			return multipartFile.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
