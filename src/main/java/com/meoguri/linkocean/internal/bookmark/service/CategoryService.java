package com.meoguri.linkocean.internal.bookmark.service;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;

public interface CategoryService {

	/* 사용자가 작성한 북마크가 존재하는 카테고리 목록 조회 */
	List<Category> getUsedCategories(long profileId);
}
