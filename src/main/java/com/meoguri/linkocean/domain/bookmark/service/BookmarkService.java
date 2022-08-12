package com.meoguri.linkocean.domain.bookmark.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;

public interface BookmarkService {

	/* 북마크 등록 */
	long registerBookmark(RegisterBookmarkCommand command);

	/* 북마크 수정 */
	void updateBookmark(UpdateBookmarkCommand command);

	/* 북마크 삭제 */
	void removeBookmark(long profileId, long bookmarkId);

	/* 북마크 상세 조회 */
	GetDetailedBookmarkResult getDetailedBookmark(long profileId, long bookmarkId);

	/* 대상의 프로필 id 로 북마크 페이징 조회 */
	Page<GetBookmarksResult> getByTargetProfileId(BookmarkFindCond findCond, Pageable pageable);

	/* 피드 북마크 목록 */
	Page<GetFeedBookmarksResult> getFeedBookmarks(BookmarkFindCond searchCond, Pageable pageable);

	/* 중복Url 확인 */
	Optional<Long> getBookmarkIdIfExist(long profileId, String url);

	/* Bookmark likeCount 수정 */
	int updateBookmarkLikeCount(long profileId, Long bookmarkLikeCount);
}
