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
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	private final BookmarkRepository bookmarkRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Transactional
	@Override
	public List<Tag> getOrSaveList(final List<String> tagNames) {
		return tagNames.stream()
			.map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName))))
			.collect(toList());
	}

	@Override
	public List<GetProfileTagsResult> getMyTags(final long profileId) {
		final Profile profile = findProfileByIdQuery.findById(profileId);

		return convert(profile);
	}

	@Override
	public List<GetProfileTagsResult> getTags(final long profileId) {
		final Profile profile = findProfileByIdQuery.findById(profileId);
		return convert(profile);
	}

	/* 프로필을 태그 조회 결과로 변환 */
	private List<GetProfileTagsResult> convert(final Profile profile) {
		final List<Bookmark> bookmarks = bookmarkRepository.findByProfileFetchTags(profile);

		final Map<String, Integer> tagCountMap = getTagCountMap(bookmarks);
		return getResult(tagCountMap);
	}

	/* 북마크를 순회하며 태그별 북마크 카운트 맵 생성 */
	private Map<String, Integer> getTagCountMap(final List<Bookmark> bookmarks) {
		final Map<String, Integer> result = new HashMap<>();
		bookmarks.stream()
			.flatMap(bookmark -> bookmark.getTagNames().stream())
			.forEach(tag -> result.put(tag, result.getOrDefault(tag, 0) + 1));
		return result;
	}

	/* 카운트 순으로 정렬하여 결과로 말아주기 */
	private List<GetProfileTagsResult> getResult(final Map<String, Integer> tagCountMap) {

		return new ArrayList<>(tagCountMap.entrySet())
			.stream()
			.sorted(Map.Entry.comparingByValue(reverseOrder()))
			.map(entry -> new GetProfileTagsResult(entry.getKey(), entry.getValue()))
			.collect(toList());
	}
}
