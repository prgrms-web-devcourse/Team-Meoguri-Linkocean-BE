package com.meoguri.linkocean.domain.linkmetadata.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.linkmetadata.entity.Link;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService;
import com.meoguri.linkocean.infrastructure.jsoup.SearchLinkMetadataResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LinkMetadataServiceImpl implements LinkMetadataService {

	private final JsoupLinkMetadataService jsoupLinkMetadataService;

	private final LinkMetadataRepository linkMetadataRepository;

	@Transactional
	@Override
	public String getOrSaveLinkMetadataTitle(final String link) {

		return linkMetadataRepository.findTitleByLink(new Link(link))
			.orElseGet(() -> {
				final SearchLinkMetadataResult result = jsoupLinkMetadataService.search(link);

				return linkMetadataRepository.save(new LinkMetadata(
					link,
					result.getTitle(),
					result.getImage())
				).getTitle();
			});
	}

	@Transactional
	@Override
	public Pageable synchronizeDataAndReturnNextPageable(final Pageable pageable) {

		final Slice<LinkMetadata> slice = linkMetadataRepository.findBy(pageable);
		slice.getContent()
			.forEach(linkMetadata -> {
				final SearchLinkMetadataResult result = jsoupLinkMetadataService.search(
					Link.toString(linkMetadata.getLink()));
				linkMetadata.update(result.getTitle(), result.getImage());
			});
		return slice.hasNext() ? slice.nextPageable() : null;
	}

}
