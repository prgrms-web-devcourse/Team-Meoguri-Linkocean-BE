package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

	@Query("select b "
		+ "from Bookmark b "
		+ "where b.profile = :profile "
		+ "and b.linkMetadata = :linkMetadata "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByProfileAndLinkMetadata(Profile profile, LinkMetadata linkMetadata);

	@Query("select b "
		+ "from Bookmark b "
		+ "where b.profile = :profile "
		+ "and b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByProfileAndId(Profile profile, long id);

	@Query("select b "
		+ "from Bookmark b "
		+ "where b.profile = :profile "
		+ "and b.url = :url "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByProfileAndUrl(Profile profile, String url);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.bookmarkTags bt "
		+ "join fetch bt.tag "
		+ "where b.profile = :profile "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	List<Bookmark> findByProfileFetchTags(Profile profile);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.profile "
		+ "join fetch b.linkMetadata "
		+ "left join fetch b.bookmarkTags bt "
		+ "left join fetch bt.tag "
		+ "where b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByIdFetchProfileAndLinkMetadataAndTags(long id);

	/**
	 * 사용자가 작성한 북마크들의 카테고리 조회
	 * @param profile
	 * @return
	 */
	@Query("select distinct b.category "
		+ "from Bookmark b "
		+ "where b.profile = :profile "
		+ "and b.category is not null")
	List<String> findCategoryExistsBookmark(Profile profile);
}
