package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindLinkMetadataByUrlQuery {

	private final LinkMetadataRepository linkMetadataRepository;

	public LinkMetadata findByUrl(final String url) {
		return linkMetadataRepository.findByLink(new Link(url))
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such linkmetadata with url : %s", url)));
	}
}
