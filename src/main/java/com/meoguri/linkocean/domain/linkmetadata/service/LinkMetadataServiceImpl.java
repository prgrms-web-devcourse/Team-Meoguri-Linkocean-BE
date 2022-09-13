package com.meoguri.linkocean.domain.linkmetadata.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.linkmetadata.service.dto.GetLinkMetadataResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LinkMetadataServiceImpl implements LinkMetadataService {

	private final GetLinkMetadata getLinkMetadata;

	private final LinkMetadataRepository linkMetadataRepository;

	@Transactional
	@Override
	public String obtainTitle(final String url) {

		return linkMetadataRepository.findTitleByLink(new Link(url))
			.orElseGet(() -> saveLinkMetadataAndReturnTitle(url));
	}

	/* url의 링크 메타데이터가 존재하면 db에 저장하고 제목을 반환한다 */
	private String saveLinkMetadataAndReturnTitle(final String url) {
		try {
			final GetLinkMetadataResult result = getLinkMetadata.getLinkMetadata(url);
			linkMetadataRepository.save(new LinkMetadata(url, result.getTitle(), result.getImage()));
			return result.getTitle();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Transactional
	@Override
	public Pageable synchronizeDataAndReturnNextPageable(final Pageable pageable) {
		final Slice<LinkMetadata> slice = linkMetadataRepository.findBy(pageable);

		slice.forEach(this::synchronizeLinkMetadata);

		return slice.hasNext() ? slice.nextPageable() : null;
	}

	private void synchronizeLinkMetadata(final LinkMetadata link) {
		try {
			final GetLinkMetadataResult result = getLinkMetadata.getLinkMetadata(Link.toString(link.getLink()));
			link.update(result.getTitle(), result.getImage());
		} catch (IllegalArgumentException ignored) {
			log.info("%s가 유효하지 않습니다.", Link.toString(link.getLink()));
		}
	}
}
