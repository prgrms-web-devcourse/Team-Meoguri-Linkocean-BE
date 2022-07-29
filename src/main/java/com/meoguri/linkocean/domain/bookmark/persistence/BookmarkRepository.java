package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByProfileAndLinkMetadata(Profile byUserId, LinkMetadata byUrl);
}
