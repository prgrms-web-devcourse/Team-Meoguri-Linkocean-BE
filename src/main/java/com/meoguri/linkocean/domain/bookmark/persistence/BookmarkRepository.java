package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByProfileAndLinkMetadata(Profile profile, LinkMetadata linkMetadata);

	Optional<Bookmark> findByProfileAndId(Profile profile, long id);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.bookmarkTags bt "
		+ "join fetch bt.tag "
		+ "where b.profile = :profile")
	List<Bookmark> findByProfileFetchTags(Profile profile);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.profile "
		+ "join fetch b.linkMetadata "
		+ "left join fetch b.bookmarkTags bt "
		+ "left join fetch bt.tag "
		+ "where b.id = :id")
	Optional<Bookmark> findByIdFetchProfileAndLinkMetadataAndTags(long id);
}
