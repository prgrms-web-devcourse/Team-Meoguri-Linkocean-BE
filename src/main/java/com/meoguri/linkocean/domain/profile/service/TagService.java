package com.meoguri.linkocean.domain.profile.service;

import java.util.List;

import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;

public interface TagService {

	/* 태그 목록 조회 */
	List<GetProfileTagsResult> getMyTags(long userId);
}
