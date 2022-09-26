package com.meoguri.linkocean.internal.profile.command.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUploader {

	/* 프로필 이미지를 업로드 하고 등록된 url 반환 */
	String upload(MultipartFile multipartFile);
}
