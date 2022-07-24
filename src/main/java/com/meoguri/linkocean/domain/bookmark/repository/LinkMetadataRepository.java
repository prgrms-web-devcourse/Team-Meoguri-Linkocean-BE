package com.meoguri.linkocean.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.LinkMetadata;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;

public interface LinkMetadataRepository extends JpaRepository<LinkMetadata, Long> {

	@Query("select l from LinkMetadata l where l.url = :url")
	Optional<LinkMetadata> findByUrl(Url url);

	@Query("select l.title from LinkMetadata l where l.url = :url")
	Optional<String> findTitleByUrl(Url url);
}
