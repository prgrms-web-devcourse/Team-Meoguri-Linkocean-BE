package com.meoguri.linkocean.internal.tag.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.internal.tag.entity.Tag;
import com.meoguri.linkocean.internal.tag.persistence.TagRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	@Transactional
	@Override
	public List<Long> getOrSaveTags(final List<String> tagNames) {
		return tagNames.stream()
			.map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName))))
			.map(Tag::getId)
			.collect(toList());
	}

	@Override
	public Set<String> getTags(final Set<Long> tagIds) {
		return tagRepository.getTags(tagIds);
	}

	@Override
	public List<String> getTags(final List<Long> tagIds) {
		return tagRepository.getTags(tagIds);
	}

	@Override
	public List<Set<String>> getTagsList(final List<Set<Long>> tagIdsList) {
		return tagRepository.getTagsList(tagIdsList);
	}
}
