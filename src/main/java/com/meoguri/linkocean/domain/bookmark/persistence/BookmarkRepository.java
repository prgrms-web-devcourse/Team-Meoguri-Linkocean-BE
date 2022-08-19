package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

	@Query("select count(b)>0 "
		+ "from Bookmark b "
		+ "where b.writer = :writer "
		+ "and b.linkMetadata = :linkMetadata "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	boolean existsByWriterAndLinkMetadata(Profile writer, LinkMetadata linkMetadata);

	@Query("select b "
		+ "from Bookmark b "
		+ "where b.id = :id "
		+ "and b.writer.id = :writerId "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByIdAndWriterId(long id, long writerId);

	@Query("select b.id "
		+ "from Bookmark b "
		+ "where b.writer.id = :writerId "
		+ "and b.url = :url "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Long> findIdByWriterIdAndUrl(long writerId, String url);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.bookmarkTags bt "
		+ "join fetch bt.tag "
		+ "where b.writer.id = :writerId "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	List<Bookmark> findByWriterIdFetchTags(long writerId);

	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.writer "
		+ "join fetch b.linkMetadata "
		+ "left join fetch b.bookmarkTags bt "
		+ "left join fetch bt.tag "
		+ "where b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByIdFetchAll(long id);

	/**
	 * @param writerId
	 * @return 사용자가 작성한 북마크들의 카테고리 조회
	 */
	@Query("select distinct b.category "
		+ "from Bookmark b "
		+ "where b.writer.id = :writerId "
		+ "and b.category is not null "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	List<Category> findCategoryExistsBookmark(long writerId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Bookmark b SET b.likeCount = b.likeCount + 1 WHERE b.id = :bookmarkId")
	int addLikeCount(long bookmarkId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Bookmark b SET b.likeCount = b.likeCount - 1 WHERE b.id = :bookmarkId")
	int subtractLikeCount(long bookmarkId);
}
