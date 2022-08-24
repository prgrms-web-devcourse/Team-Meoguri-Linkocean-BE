package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;

public interface CustomBookmarkRepository {

	boolean existsByWriterAndLinkMetadata(Profile writer, LinkMetadata linkMetadata);

	/* 대상의 프로필 id 로 북마크 페이징 조회 */
	Page<Bookmark> findByTargetProfileId(BookmarkFindCond findCond, Pageable pageable);

	/* 피드 북마크 조회 */
	Page<Bookmark> findBookmarks(BookmarkFindCond findCond, Pageable pageable);

	/* 북마크의 리액션 별 카운트 조회 */
	Map<ReactionType, Long> countReactionGroup(long bookmarkId);
}
