package com.meoguri.linkocean.domain.profile.service;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class TagServiceImpl implements TagService {

	private final BookmarkRepository bookmarkRepository;

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;

	@Override
	public List<GetProfileTagsResult> getMyTags(final long userId) {
		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);
		final List<Bookmark> bookmarks = bookmarkRepository.findByProfileFetchTags(profile);

		final Map<String, Integer> tagCountMap = getTagCountMap(bookmarks);
		return getResult(tagCountMap);
	}

	/* 카운트 순으로 정렬하여 결과로 말아주기 */
	private List<GetProfileTagsResult> getResult(final Map<String, Integer> tagCountMap) {

		return new ArrayList<>(tagCountMap.entrySet())
			.stream()
			.sorted(Map.Entry.comparingByValue(reverseOrder()))
			.map(entry -> new GetProfileTagsResult(entry.getKey(), entry.getValue()))
			.collect(toList());
	}

	/* 북마크를 순회하며 태그별 북마크 카운트 맵 생성*/
	private Map<String, Integer> getTagCountMap(final List<Bookmark> bookmarks) {
		final Map<String, Integer> result = new HashMap<>();
		bookmarks.stream()
			.flatMap(bookmark -> bookmark.getTagNames().stream())
			.forEach(tag -> result.put(tag, result.getOrDefault(tag, 0) + 1));
		return result;
	}
}
