package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.BookmarkByUsernameSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;

public interface BookmarkService {

	/* 북마크 등록 */
	long registerBookmark(RegisterBookmarkCommand command);

	/* 북마크 수정 */
	void updateBookmark(UpdateBookmarkCommand command);

	/* 북마크 상세 조회 */
	GetBookmarkResult getBookmark(long userId, long bookmarkId);

	/* 내 북마크 목록 */
	List<GetBookmarksResult> getMyBookmarks(MyBookmarkSearchCond searchCond);

	/* 피드 북마크 목록 */
	List<GetFeedBookmarksResult> getFeedBookmarks(FeedBookmarksSearchCond searchCond);

	// TODO - 구현
	/* 다른 사람 북마크 목록 */
	List<GetBookmarksResult> getBookmarksByUsername(BookmarkByUsernameSearchCond searchCond);
}
