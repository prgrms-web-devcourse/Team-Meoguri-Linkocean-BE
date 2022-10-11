package com.meoguri.linkocean.infrastructure.s3;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.meoguri.linkocean.internal.profile.command.service.ProfileImageUploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3ProfileImageUploader implements ProfileImageUploader {

	private static final String PROFILE_IMAGE_SAVE_PATH = "profile";

	private final S3Uploader s3Uploader;

	@Override
	public String upload(final MultipartFile multipartFile) {
		return s3Uploader.upload(multipartFile, PROFILE_IMAGE_SAVE_PATH);
	}
}
