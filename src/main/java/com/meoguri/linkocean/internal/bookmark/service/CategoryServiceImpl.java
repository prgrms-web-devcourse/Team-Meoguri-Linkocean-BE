package com.meoguri.linkocean.internal.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.persistence.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final BookmarkRepository bookmarkRepository;

	@Override
	public List<Category> getUsedCategories(final long profileId) {
		return bookmarkRepository.findCategoryExistsBookmark(profileId);
	}

}
