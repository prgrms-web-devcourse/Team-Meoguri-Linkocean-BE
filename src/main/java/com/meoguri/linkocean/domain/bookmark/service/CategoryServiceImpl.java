package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final BookmarkRepository bookmarkRepository;

	@Override
	public List<String> getAllCategories() {
		return Category.getKoreanNames();
	}

	@Override
	public List<String> getUsedCategories(final long userId) {
		final Profile writer = findProfileByUserIdQuery.findByUserId(userId);

		return bookmarkRepository
			.findCategoryExistsBookmark(writer).stream()
			.map(name -> Category.valueOf(name).getName())
			.collect(Collectors.toList());
	}
}
