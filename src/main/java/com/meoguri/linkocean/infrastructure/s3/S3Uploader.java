package com.meoguri.linkocean.infrastructure.s3;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.lang.String.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
	public String upload(MultipartFile multipartFile, String dirName) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		return upload(convert(multipartFile), dirName);
	}

	private File convert(MultipartFile multipartFile) {
		final String originalFilename = multipartFile.getOriginalFilename();
		checkNotNull(originalFilename);

		final File convertFile = new File(originalFilename);
		try {
			if (convertFile.createNewFile()) {
				try (FileOutputStream fos = new FileOutputStream(convertFile)) {
					fos.write(multipartFile.getBytes());
				}
				return convertFile;
			} else {
				throw new IllegalStateException(format("the named file already exists : %s", originalFilename));
			}
		} catch (IOException e) {
			log.info("failed to convert MultipartFile with original file name : {} to File", originalFilename);
			throw new RuntimeException(e);
		}
	}

	private String upload(File file, String dirName) {
		final String saveFilePath = getSaveFilePath(file, dirName);

		amazonS3Client.putObject(new PutObjectRequest(bucket, saveFilePath, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));

		/* local 에 남는 파일 삭제 */
		final String imageUrl = amazonS3Client.getUrl(bucket, saveFilePath).toString();
		file.delete();
		return imageUrl;
	}

	private String getSaveFilePath(final File file, final String dirName) {
		final String extension = FilenameUtils.getExtension(file.getName());
		checkArgument(EXTENSIONS_IMAGE.contains(extension), "유효하지 않은 파일 형식입니다.");

		final String saveFilename = String.join(".", UUID.randomUUID().toString(), extension);
		return String.join("/", dirName, saveFilename);
	}
}
