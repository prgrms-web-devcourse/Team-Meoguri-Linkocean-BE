package com.meoguri.linkocean.infrastructure.s3;

import java.io.File;
import java.io.FileOutputStream;
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
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		return upload(convert(multipartFile), dirName);
	}

	private File convert(MultipartFile multipartFile) {
		final String originalFilename = multipartFile.getOriginalFilename();
		File convertFile = new File(multipartFile.getOriginalFilename());
		try {
			if (convertFile.createNewFile()) {
				try (FileOutputStream fos = new FileOutputStream(convertFile)) {
					fos.write(multipartFile.getBytes());
				}
				return convertFile;
			}
		} catch (IOException e) {
			log.info("failed to convert MultipartFile with original file name : {} to File", originalFilename);
			throw new RuntimeException(e);
		}
		return null;
	}

	//TODO 이미지 url 겹치지 않게 로직 작성
	private String upload(File file, String dirName) {
		String fileName = dirName + "/" + file.getName();
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		final String uploadUrl = amazonS3Client.getUrl(bucket, fileName).toString();
		file.delete(); // local 에 남는 파일을 지우기 위한 용도
		return uploadUrl;
	}
}
