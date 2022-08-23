package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

	@Query("select count(b)>0 "
		+ "from Bookmark b "
		+ "where b.writer = :writer "
		+ "and b.linkMetadata = :linkMetadata "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	boolean existsByWriterAndLinkMetadata(Profile writer, LinkMetadata linkMetadata);

	/* 아이디와 작성자로 조회 */
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

	/* 작성자의 아이디로 태그페치 조회 */
	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.tags t "
		+ "where b.writer.id = :writerId "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	List<Bookmark> findByWriterIdFetchTags(long writerId);

	/* 아이디로 전체 페치 조회 */
	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.writer "
		+ "join fetch b.linkMetadata "
		+ "left join fetch b.tags t "
		+ "where b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Optional<Bookmark> findByIdFetchAll(long id);

	/* 사용자가 작성한 북마크들의 카테고리 조회 */
	@Query("select distinct b.category "
		+ "from Bookmark b "
		+ "where b.writer.id = :writerId "
		+ "and b.category is not null "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	List<Category> findCategoryExistsBookmark(long writerId);

	default void updateLikeCount(long bookmarkId, ReactionType existedType, ReactionType requestType) {
		if (requestType.equals(LIKE)) {
			if (existedType == LIKE) {
				/* like 를 두번 요청하여 취소 */
				subtractLikeCount(bookmarkId);
			} else {
				/* like 등록 혹은 hate -> like 변경 */
				addLikeCount(bookmarkId);
			}
		} else if (requestType.equals(HATE)) {
			if (existedType == LIKE) {
				/* like -> hate 변경 */
				subtractLikeCount(bookmarkId);
			}
		}
	}

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update Bookmark b set b.likeCount = b.likeCount + 1 where b.id = :bookmarkId")
	void addLikeCount(long bookmarkId);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update Bookmark b set b.likeCount = b.likeCount - 1 where b.id = :bookmarkId")
	void subtractLikeCount(long bookmarkId);
}
