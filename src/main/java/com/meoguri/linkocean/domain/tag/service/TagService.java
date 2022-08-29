package com.meoguri.linkocean.domain.tag.service;

import java.util.List;
import java.util.Set;

import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfileTagsResult;

public interface TagService {

	/* 태그 목록 조회 */
	List<GetProfileTagsResult> getTags(long profileId);

	/*
	 * 태그 조회 혹은 추가
	 * TagName 정보를 이용해 Tag 를 만든다.
	 * 1. tag 이름이 존재하면 만들지 않고 db 에서 가져온다.
	 * 2. tag 이름이 존재하지 않다면 태그를 만들고 db 에 저장한다.
	 * 생성/조회 된 태그의 아이디 목록 반환
	 */
	List<Long> getOrSaveTags(List<String> tagNames);

	Set<String> getTags(Set<Long> tagIds);

	List<Set<String>> getTagsList(List<Set<Long>> tagIdsList);
}
