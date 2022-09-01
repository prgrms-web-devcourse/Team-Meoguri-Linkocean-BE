package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindUsedTagIdWithCountResult;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface CustomBookmarkRepository {

	boolean existsByWriterAndLinkMetadata(Profile writer, Long linkMetadata);

	/* 피드 북마크 조회 */
	Page<Bookmark> findBookmarks(BookmarkFindCond findCond, Pageable pageable);

	/* 사용한 북마크 태그 별 사용 카운트 조회 */
	List<FindUsedTagIdWithCountResult> findUsedTagIdsWithCount(long profileId);
}
