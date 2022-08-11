package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.controller.common.SimpleIdResponse.of;
import static java.time.LocalDateTime.*;
import static java.util.stream.Collectors.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.resolver.GetBookmarkQueryParams;
import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.bookmark.dto.GetBookmarksResponse;
import com.meoguri.linkocean.controller.bookmark.dto.GetDetailedBookmarkResponse;
import com.meoguri.linkocean.controller.bookmark.dto.GetFeedBookmarksResponse;
import com.meoguri.linkocean.controller.bookmark.dto.GetFeedBookmarksResponse.ProfileResponse;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.bookmark.dto.UpdateBookmarkRequest;
import com.meoguri.linkocean.controller.common.PageResponse;
import com.meoguri.linkocean.controller.common.SimpleIdResponse;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.BookmarkService;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;

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
		return of(bookmarkService.registerBookmark(request.toCommand(user.getProfileId())));
	}

	/* 내 북마크 목록 조회 */
	@GetMapping("/me")
	public PageResponse<GetBookmarksResponse> getMyBookmarks(
		final @AuthenticationPrincipal SecurityUser user,
		final GetBookmarkQueryParams queryParams
	) {
		return getByTargetProfileId(user, user.getProfileId(), queryParams);
	}

	/**
	 * 대상의 프로필 id 로 북마크 페이징 조회
	 * 카테고리 필터링, 제목 필터링, 태그 필터링, 즐겨찾기 필터링을 지원한다 <br>
	 */
	@GetMapping("/others/{profileId}")
	public PageResponse<GetBookmarksResponse> getByTargetProfileId(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable("profileId") long targetProfileId,
		final GetBookmarkQueryParams queryParams
	) {
		final Page<GetBookmarksResult> result = bookmarkService.getByTargetProfileId(
			new BookmarkFindCond(
				user.getId(),
				targetProfileId,
				queryParams.getCategory(),
				queryParams.isFavorite(),
				queryParams.getTags(),
				queryParams.isFollow(),
				queryParams.getTitle()
			),
			queryParams.toPageable()
		);

		final List<GetBookmarksResponse> response = result.get()
			.map(GetBookmarksResponse::of)
			.collect(toList());
		return PageResponse.of(result.getTotalElements(), "bookmarks", response);
	}

	/**
	 * 피드 북마크 목록 조회
	 * - 북마크 정보와 함께 작성자 프로필 정보를 반환한다
	 */
	@GetMapping("/feed")
	public PageResponse<GetFeedBookmarksResponse> getFeedBookmarks(
		final @AuthenticationPrincipal SecurityUser user,
		final GetBookmarkQueryParams queryParams
	) {
		final Page<GetFeedBookmarksResult> result = bookmarkService.getFeedBookmarks(
			new BookmarkFindCond(
				user.getId(),
				null, //작성자가 따로 없는 조회 이므로 null
				queryParams.getCategory(),
				queryParams.isFavorite(),
				queryParams.getTags(),
				queryParams.isFollow(),
				queryParams.getTitle()
			),
			queryParams.toPageable()
		);

		final List<GetFeedBookmarksResponse> response = result.get()
			.map(GetFeedBookmarksResponse::of)
			.collect(toList());
		return PageResponse.of(response.size(), "bookmarks", response);
	}

	/* 북마크 상세 조회 */
	@GetMapping("/{bookmarkId}")
	public GetDetailedBookmarkResponse getDetailedBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(user.getProfileId(), bookmarkId);
		return GetDetailedBookmarkResponse.of(result);
	}

	/* 북마크 업데이트 */
	@PutMapping("/{bookmarkId}")
	public void updateBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody UpdateBookmarkRequest request,
		final @PathVariable long bookmarkId
	) {
		bookmarkService.updateBookmark(request.toCommand(user.getProfileId(), bookmarkId));
	}

	/* 북마크 삭제 */
	@DeleteMapping("/{bookmarkId}")
	public void removeBookmark(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		bookmarkService.removeBookmark(user.getProfileId(), bookmarkId);
	}

	/* 중복 url 확인 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> getBookmarkIdIfDuplicated(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestParam("url") String url
	) {
		final Optional<Long> oBookmarkId = bookmarkService.getBookmarkIdIfExist(user.getProfileId(), url);

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccessControlAllowHeaders(List.of("*"));
		httpHeaders.setAccessControlExposeHeaders(List.of("*"));
		httpHeaders.setAccessControlRequestHeaders(List.of("*"));
		return ResponseEntity.ok()
			.headers(httpHeaders)
			.headers(getLocationHeaderIfPresent(oBookmarkId))
			.body(Map.of("isDuplicateUrl", oBookmarkId.isPresent()));
	}

	private HttpHeaders getLocationHeaderIfPresent(final Optional<Long> oBookmarkId) {
		HttpHeaders headers = new HttpHeaders();
		oBookmarkId.ifPresent(bookmarkId -> headers.setLocation(URI.create("api/v1/bookmarks/" + bookmarkId)));
		return headers;
	}

	private PageResponse<GetFeedBookmarksResponse> feedDummyData() {

		return PageResponse.of(2, "bookmarks", List.of(
			new GetFeedBookmarksResponse(
				1L,
				"네이버 웹툰",
				"https://comic.naver.com/index",
				"all",
				"IT",
				now(),
				10L,
				true,
				false,
				"bookmarkImageUrl",
				List.of("spring", "fun"),
				new ProfileResponse(
					1L, "crush", "profileImage.png", false
				)
			),
			new GetFeedBookmarksResponse(
				2L,
				"다음 웹툰",
				"https://comic.daum.com/index",
				"all",
				null,
				now(),
				10L,
				false,
				true,
				"bookmarkImageUrl2",
				List.of("spring", "fun"),
				new ProfileResponse(
					1L, "crush", "profileImageUrl", false
				)
			)
		));
	}
}
