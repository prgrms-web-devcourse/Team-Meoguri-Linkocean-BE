package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static java.lang.String.*;

import java.util.List;
import java.util.Set;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;
import com.meoguri.linkocean.support.domain.persistence.Query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindLinkMetadataByIdQuery {

	private final LinkMetadataRepository linkMetadataRepository;

	public LinkMetadata findById(final Long id) {
		return linkMetadataRepository.findById(id)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such linkmetadata with id : %d", id)));
	}

	public Set<LinkMetadata> findByIds(final List<Long> ids) {
		return linkMetadataRepository.findByIds(ids);
	}
}
