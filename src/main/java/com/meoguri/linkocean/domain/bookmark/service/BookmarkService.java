package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.OtherBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.PageResult;
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

	/* 내 북마크 목록 */
	Page<GetBookmarksResult> getMyBookmarks(MyBookmarkSearchCond searchCond, Pageable pageable);

	// TODO - 구현
	/* 다른 사람 북마크 목록 */
	PageResult<GetBookmarksResult> getOtherBookmarks(long profileId, OtherBookmarkSearchCond searchCond);

	/* 피드 북마크 목록 */
	List<GetFeedBookmarksResult> getFeedBookmarks(FeedBookmarksSearchCond searchCond);

	/* 중복Url 확인 */
	Optional<Long> getBookmarkToCheck(long userId, String url);
}
