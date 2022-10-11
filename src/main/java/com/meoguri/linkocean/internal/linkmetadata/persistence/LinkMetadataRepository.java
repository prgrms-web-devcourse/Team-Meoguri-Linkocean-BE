package com.meoguri.linkocean.internal.linkmetadata.persistence;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.internal.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.internal.linkmetadata.entity.vo.Link;

public interface LinkMetadataRepository extends JpaRepository<LinkMetadata, Long> {

	@Query("select l.title "
		+ "from LinkMetadata l "
		+ "where l.link = :link")
	Optional<String> findTitleByLink(Link link);

	/* 배치작업 최적화를 위해 Slice 이용 */
	Slice<LinkMetadata> findBy(Pageable pageable);

}
