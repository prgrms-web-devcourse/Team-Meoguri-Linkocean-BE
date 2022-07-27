package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;

public interface BookmarkService {

	/* 북마크 등록 */
	long registerBookmark(RegisterBookmarkCommand command);

	/* 북마크 상세 조회 */
	GetBookmarkResult getBookmark(long userId, long bookmarkId);

	/* 내 북마크 목록 */
	List<GetMyBookmarksResult> getMyBookmarks(MyBookmarkSearchCond searchCond);

	/* 피드 북마크 목록 */
	List<GetFeedBookmarksResult> getFeedBookmarks(FeedBookmarkSearchCond searchCond);
}
