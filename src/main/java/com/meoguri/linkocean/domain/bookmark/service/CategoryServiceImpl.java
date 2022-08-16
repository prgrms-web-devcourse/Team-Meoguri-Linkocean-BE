package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final BookmarkRepository bookmarkRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public List<Category> getUsedCategories(final long profileId) {
		return bookmarkRepository.findCategoryExistsBookmark(profileId);
	}

}
