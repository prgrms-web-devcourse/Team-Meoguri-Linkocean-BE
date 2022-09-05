package com.meoguri.linkocean.domain.linkmetadata.persistence;

import java.util.Optional;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindLinkMetadataByUrlQuery {

	private final LinkMetadataRepository linkMetadataRepository;

	public Optional<LinkMetadata> findByUrl(final String url) {
		return linkMetadataRepository.findByLink(new Link(url));
	}
}
