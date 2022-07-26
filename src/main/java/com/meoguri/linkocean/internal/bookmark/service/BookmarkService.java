package com.meoguri.linkocean.internal.bookmark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.internal.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetUsedTagWithCountResult;
import com.meoguri.linkocean.internal.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.internal.bookmark.service.dto.UpdateBookmarkCommand;

public interface BookmarkService {

	/* 북마크 등록 */
	long registerBookmark(RegisterBookmarkCommand command);

	/* 북마크 수정 */
	void updateBookmark(UpdateBookmarkCommand command);

	/* 북마크 삭제 */
	void removeBookmark(long writerId, long bookmarkId);

	/* 북마크 상세 조회 */
	GetDetailedBookmarkResult getDetailedBookmark(long profileId, long bookmarkId);

	/* 대상의 프로필 id 로 북마크 페이징 조회 */
	Page<GetBookmarksResult> getByTargetProfileId(BookmarkFindCond findCond, Pageable pageable);

	/* 피드 북마크 목록 */
	Page<GetFeedBookmarksResult> getFeedBookmarks(BookmarkFindCond searchCond, Pageable pageable);

	/* 북마크 공유 알림 */
	void shareNotification(long profileId, long targetId, long bookmarkId);

	/* 중복Url 확인 */
	Optional<Long> getBookmarkIdIfExist(long profileId, String url);

	/* 태그 목록 조회 */
	List<GetUsedTagWithCountResult> getUsedTagsWithCount(long profileId);

}
