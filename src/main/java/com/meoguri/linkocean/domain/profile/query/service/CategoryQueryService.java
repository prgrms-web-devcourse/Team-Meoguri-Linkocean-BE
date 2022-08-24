package com.meoguri.linkocean.domain.profile.query.service;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

public interface CategoryQueryService {

	/* 사용자가 작성한 북마크가 존재하는 카테고리 목록 조회 */
	List<Category> getUsedCategories(long profileId);
}
