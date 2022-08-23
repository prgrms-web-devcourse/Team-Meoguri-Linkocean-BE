package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.profile.service.command.dto.GetProfileTagsResult;

public interface TagService {


	/* 태그 목록 조회 */
	List<GetProfileTagsResult> getTags(long profileId);

	/*
	 * 태그 조회 혹은 추가
	 * TagName 정보를 이용해 Tag 를 만든다.
	 * 1. tag 이름이 존재하면 만들지 않고 db 에서 가져온다.
	 * 2. tag 이름이 존재하지 않다면 태그를 만들고 db 에 저장한다.
	 */
	List<Tag> getOrSaveTags(List<String> tagNames);
}
