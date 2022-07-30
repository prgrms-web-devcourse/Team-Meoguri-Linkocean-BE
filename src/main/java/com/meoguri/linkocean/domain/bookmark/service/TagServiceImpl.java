package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyTagsResult;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final BookmarkRepository bookmarkRepository;

	@Override
	public List<GetMyTagsResult> getMyTags(final long userId) {

		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);
		final List<Bookmark> bookmarks = bookmarkRepository.findByProfileFetchTags(profile);

		//TODO - 메모리에서 Result 말아주는 로직 짜기
		return null;
	}
}
