package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.controller.common.SimpleIdResponse.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.resolver.GetBookmarkQueryParams;
import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.bookmark.dto.GetBookmarksResponse;
import com.meoguri.linkocean.controller.bookmark.dto.GetDetailedBookmarkResponse;
import com.meoguri.linkocean.controller.bookmark.dto.GetFeedBookmarksResponse;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.bookmark.dto.UpdateBookmarkRequest;
import com.meoguri.linkocean.controller.common.PageResponse;
import com.meoguri.linkocean.controller.common.SimpleIdResponse;
import com.meoguri.linkocean.domain.bookmark.service.BookmarkService;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.PageResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class BookmarkController {

	private final BookmarkService bookmarkService;

	/* 북마크 등록 */
	@PostMapping
	public SimpleIdResponse registerBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody RegisterBookmarkRequest request
	) {
		return of(bookmarkService.registerBookmark(request.toCommand(user.getId())));
	}

	/**
	 * 내 북마크 목록 조회
	 * - 프로필이 없다
	 * - 내 북마크 목록 조회와 다른 사람 북마크 목록 조회로 나뉜다
	 */
	@GetMapping("/me")
	public PageResponse<GetBookmarksResponse> getMyBookmarks(
		final @AuthenticationPrincipal SecurityUser user,
		final GetBookmarkQueryParams queryParams
	) {
		final PageResult<GetBookmarksResult> result = bookmarkService.getMyBookmarks(user.getId(),
			queryParams.toMySearchCond(user.getId()));

		final List<GetBookmarksResponse> response = result.getData()
			.stream()
			.map(GetBookmarksResponse::of)
			.collect(toList());
		return PageResponse.of(result.getTotalCount(), "bookmarks", response);
	}

	//TODO (초벌 상태)
	@GetMapping("/others/{profileId}")
	public PageResponse<GetBookmarksResponse> getOtherBookmarks(
		final @AuthenticationPrincipal SecurityUser user,
		final GetBookmarkQueryParams queryParams
	) {
		final PageResult<GetBookmarksResult> result = bookmarkService.getOtherBookmarks(user.getId(),
			queryParams.toUsernameSearchCond(null));

		final List<GetBookmarksResponse> response = result.getData()
			.stream()
			.map(GetBookmarksResponse::of)
			.collect(toList());
		return PageResponse.of(result.getTotalCount(), "bookmarks", response);
	}

	/**
	 * 피드 북마크 목록 조회
	 * - 북마크 정보와 함께 작성자 프로필 정보를 반환한다
	 */
	//TODO
	@GetMapping("/feed")
	public PageResponse<GetFeedBookmarksResponse> getFeedBookmarks(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody GetBookmarkQueryParams queryParams
	) {
		final List<GetFeedBookmarksResult> result = bookmarkService.getFeedBookmarks(queryParams.toFeedSearchCond());

		final List<GetFeedBookmarksResponse> response =
			result.stream().map(GetFeedBookmarksResponse::of).collect(toList());
		return PageResponse.of(10, "bookmarks", response);
	}

	/* 북마크 상세 조회 */
	@GetMapping("/{bookmarkId}")
	public GetDetailedBookmarkResponse getDetailedBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(user.getId(), bookmarkId);
		return GetDetailedBookmarkResponse.of(result);
	}

	/* 북마크 업데이트 */
	@PutMapping("/{bookmarkId}")
	public void updateBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody UpdateBookmarkRequest request,
		final @PathVariable long bookmarkId
	) {
		bookmarkService.updateBookmark(request.toCommand(user.getId(), bookmarkId));
	}

	/* 북마크 삭제 */
	@DeleteMapping("/{bookmarkId}")
	public void deleteBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		// TODO - 구현
		// bookmarkService.deleteBookmark(user.getId(), bookmarkId);
	}
}
