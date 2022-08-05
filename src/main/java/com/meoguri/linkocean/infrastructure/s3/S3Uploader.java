package com.meoguri.linkocean.infrastructure.s3;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class S3Uploader {

	private final AmazonS3Client amazonS3Client;

	private final String bucket;

	/**
	 * multipartFile 을 S3 버킷의 directory 에 저장한다.
	 * 주어진 multipartFile 이 저장된 imageUrl 을 반환한다.
	 * 주어진 multipartFile 이 null 이라면 null 을 반환한다.
	 */
	public String upload(MultipartFile multipartFile, String dirName) {
		if (multipartFile == null) {
			return null;
		}
		return upload(convert(multipartFile), dirName);
	}

	private File convert(MultipartFile multipartFile) {
		final String originalFilename = multipartFile.getOriginalFilename();
		checkNotNull(originalFilename);
		try {
			final File file = new File(originalFilename);
			multipartFile.transferTo(file);
			return file;
		} catch (IOException e) {
			log.info("failed to convert MultipartFile with original file name : {} to File", originalFilename);
			throw new RuntimeException(e);
		}
	}

	private String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
}

