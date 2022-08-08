package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final BookmarkRepository bookmarkRepository;

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public List<String> getMyUsedCategories(final long userId) {
		final Profile writer = findProfileByUserIdQuery.findByUserId(userId);

		return convert(writer);
	}

	@Override
	public List<String> getUsedCategories(final long profileId) {
		final Profile writer = findProfileByIdQuery.findById(profileId);

		return convert(writer);
	}

	private List<String> convert(final Profile writer) {
		return bookmarkRepository.findCategoryExistsBookmark(writer).stream()
			.map(name -> Category.valueOf(name).getKorName())
			.collect(Collectors.toList());
	}
}
