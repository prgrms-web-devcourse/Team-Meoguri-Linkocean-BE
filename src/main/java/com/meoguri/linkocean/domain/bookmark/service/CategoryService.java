package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

public interface CategoryService {

	/* 자신이 작성한 북마크가 존재하는 카테고리 목록 조회 */
	List<String> getMyUsedCategories(long userId);

	/* 사용자가 작성한 북마크가 존재하는 카테고리 목록 조회 */
	List<String> getUsedCategories(long profileId);

}
