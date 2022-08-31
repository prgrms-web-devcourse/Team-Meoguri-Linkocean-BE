package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindUsedTagIdWithCountResult;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.profile.command.entity.vo.ReactionType;

public interface CustomBookmarkRepository {

	boolean existsByWriterAndLinkMetadata(Profile writer, Long linkMetadata);

	/* 피드 북마크 조회 */
	Page<Bookmark> findBookmarks(BookmarkFindCond findCond, Pageable pageable);

	/* 북마크의 리액션 별 카운트 조회 */
	Map<ReactionType, Long> countReactionGroup(long bookmarkId);

	/* 사용한 북마크 태그 별 사용 카운트 조회 */
	List<FindUsedTagIdWithCountResult> findUsedTagIdsWithCount(long profileId);
}
