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

	@Query("select distinct b from Bookmark b join fetch b.bookmarkTags bt join fetch bt.tag where b.profile = :profile")
	List<Bookmark> findByProfileFetchTags(Profile profile);
}
