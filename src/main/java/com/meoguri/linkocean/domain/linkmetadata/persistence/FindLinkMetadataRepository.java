package com.meoguri.linkocean.domain.linkmetadata.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;

public interface FindLinkMetadataRepository extends Repository<LinkMetadata, Long> {

	LinkMetadata findById(long id);

	@Query("select l "
		+ "from LinkMetadata l "
		+ "where l.id in :ids")
	Set<LinkMetadata> findByIds(List<Long> ids);

	LinkMetadata findByLink(Link link);
}
