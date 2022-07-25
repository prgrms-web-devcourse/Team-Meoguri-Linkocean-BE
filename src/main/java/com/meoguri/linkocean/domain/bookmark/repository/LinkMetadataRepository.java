package com.meoguri.linkocean.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.LinkMetadata;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;

public interface LinkMetadataRepository extends JpaRepository<LinkMetadata, Long> {

	Optional<LinkMetadata> findByUrl(Url url);

	@Query("select l.title from LinkMetadata l where l.url = :url")
	Optional<String> findTitleByUrl(Url url);

	/* 배치작업 최적화를 위해 Slice 이용 */
	Slice<LinkMetadata> findBy(Pageable pageable);
}
