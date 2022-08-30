package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.profile.command.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetUsedTagWithCountResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

class BookmarkServiceImplTest extends BaseServiceTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Nested
	class 북마크_등록 {

		private long profileId;

		@BeforeEach
		void setUp() {
			profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
		}

		@Test
		void 북마크_등록_성공() {
			//given
			링크_제목_얻기("www.naver.com");
			final RegisterBookmarkCommand command =
				new RegisterBookmarkCommand(profileId, "www.naver.com", "네이버", "memo", IT, ALL,
					List.of("tag1", "tag2"));

			//when
			final long registeredBookmarkId = bookmarkService.registerBookmark(command);

			//then
			final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, registeredBookmarkId);
			assertThat(result.getBookmarkId()).isEqualTo(registeredBookmarkId);
			assertThat(result.getUrl()).isEqualTo("www.naver.com");
			assertThat(result.getTitle()).isEqualTo("네이버");
			assertThat(result.getMemo()).isEqualTo("memo");
			assertThat(result.getCategory()).isEqualTo(IT);
			assertThat(result.getOpenType()).isEqualTo(ALL);
			assertThat(result.getTags()).containsExactlyInAnyOrder("tag1", "tag2");

			assertThat(result.getReactionCount()).containsAllEntriesOf(Map.of(LIKE, 0L, HATE, 0L));
			assertThat(result.getReaction()).containsAllEntriesOf(Map.of(LIKE, false, HATE, false));

			assertThat(result.getProfile().getProfileId()).isEqualTo(profileId);
			assertThat(result.getProfile().getUsername()).isEqualTo("haha");
			assertThat(result.getProfile().getImage()).isEqualTo(null);
			assertThat(result.getProfile().isFollow()).isEqualTo(false);
		}

		@Test
		void 북마크_등록_실패_유효하지_않은_사용자() {
			//given
			final long invalidId = -1L;

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> 북마크_등록(invalidId, "www.youtube.com"));
		}

		@Test
		void 중복_url_북마크_생성_요청에_따라_실패() {
			//given
			북마크_등록(profileId, "www.youtube.com");

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> 북마크_등록(profileId, "www.youtube.com"));
		}

	}

	@Nested
	class 북마크_업데이트_테스트 {

		private long profileId;
		private long bookmarkId;

		@BeforeEach
		void setUp() {
			profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
			bookmarkId = 북마크_등록(profileId, "www.youtube.com");
		}

		@Test
		void 북마크_업데이트_성공() {
			//given
			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				profileId,
				bookmarkId,
				"updatedTitle",
				"updatedMemo",
				HUMANITIES,
				PRIVATE,
				List.of("tag1", "tag2")
			);

			//when
			bookmarkService.updateBookmark(command);

			final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
			assertThat(result.getBookmarkId()).isEqualTo(bookmarkId);
			assertThat(result.getUrl()).isEqualTo("www.youtube.com");
			assertThat(result.getTitle()).isEqualTo("updatedTitle");
			assertThat(result.getMemo()).isEqualTo("updatedMemo");
			assertThat(result.getCategory()).isEqualTo(HUMANITIES);
			assertThat(result.getOpenType()).isEqualTo(PRIVATE);
			assertThat(result.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
		}

		@Test
		void 북마크_업데이트_실패_존재하지_않는_사용자() {
			//given
			final long invalidProfileId = -1;
			final UpdateBookmarkCommand command = createUpdateBookmarkCommand(invalidProfileId, bookmarkId);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		@Test
		void 북마크_업데이트_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = -1L;
			final UpdateBookmarkCommand command = createUpdateBookmarkCommand(profileId, invalidBookmarkId);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		@Test
		void 북마크_업데이트_실패_다른_사용자의_북마크() {
			//given
			final long crushProfileId = 사용자_프로필_동시_등록("crush@gmail.com", GOOGLE, "crush", IT);
			final long crushBookmarkId = 북마크_등록(crushProfileId, "www.linkocean.com");

			final UpdateBookmarkCommand command = createUpdateBookmarkCommand(profileId, crushBookmarkId);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		private UpdateBookmarkCommand createUpdateBookmarkCommand(final long profileId, final long bookmarkId) {
			return new UpdateBookmarkCommand(
				profileId,
				bookmarkId,
				"updatedTitle",
				"updatedMemo",
				HUMANITIES,
				PRIVATE,
				List.of("tag1", "tag2")
			);
		}
	}

	@Nested
	class 북마크_삭제 {

		private long profileId;
		private long bookmarkId;

		@BeforeEach
		void setUp() {
			profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
			bookmarkId = 북마크_등록(profileId, "www.youtube.com");
		}

		@Test
		void 북마크_삭제_성공_조회_되지_않음() {
			//when
			bookmarkService.removeBookmark(profileId, bookmarkId);

			//then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.getDetailedBookmark(profileId, bookmarkId));
		}

		@Test
		void 북마크_삭제_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = -1L;

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.removeBookmark(profileId, invalidBookmarkId));
		}

		@Test
		void 북마크_삭제_실패_다른_사용자의_북마크_삭제_시도() {
			//given
			final long crushProfileId = 사용자_프로필_동시_등록("crush@gmail.com", GOOGLE, "crush", IT);
			final long crushBookmarkId = 북마크_등록(crushProfileId, "www.linkocean.com");

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.removeBookmark(profileId, crushBookmarkId));
		}
	}

	@Nested
	class 북마크_상세_조회 {

		private long profileId;
		private long bookmarkId;

		@BeforeEach
		void setUp() {
			profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
			bookmarkId = 북마크_등록(profileId, "www.youtube.com");
		}

		@Test
		void 북마크_상세_조회_성공_좋아요_북마크() {
			//given
			좋아요_요청(profileId, bookmarkId);

			//when
			final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(profileId, bookmarkId);

			//then
			assertThat(result.getReactionCount()).containsAllEntriesOf(Map.of(LIKE, 1L, HATE, 0L));
			assertThat(result.getReaction()).containsAllEntriesOf(Map.of(LIKE, true, HATE, false));
		}

		@Test
		void 북마크_상세_조회_성공_즐겨찾기_북마크() {
			//given
			즐겨찾기(profileId, bookmarkId);

			//when
			final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(profileId, bookmarkId);

			//then
			assertThat(result.isFavorite()).isEqualTo(true);
		}

		@Test
		void 북마크_상세_조회_실패_유효하지_않은_북마크() {
			//given
			final long invalidBookmarkId = -1L;

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> 북마크_상세_조회(profileId, invalidBookmarkId));
		}

	}

	@Nested
	class 대상의_프로필_id로_북마크_페이징_조회 {

		private long profileId1;
		private long profileId2;

		private long bookmarkId1;
		private long bookmarkId2;
		private long bookmarkId3;

		@BeforeEach
		void setUp() {
			profileId1 = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
			bookmarkId1 = 북마크_등록(profileId1, "http://www.naver.com", "title1", null, IT, ALL, "tag1", "tag2");
			bookmarkId2 = 북마크_등록(profileId1, "http://www.daum.com", "title2", null, IT, PARTIAL, "tag2");
			bookmarkId3 = 북마크_등록(profileId1, "http://www.kakao.com", "title3", null, HOME, PRIVATE, "tag1");

			profileId2 = 사용자_프로필_동시_등록("crush@gmail.com", GOOGLE, "crush", IT);
		}

		/* 다른 사람과 팔로우/팔로이 관계가 아니므로 공개 범위가 all 인 글만 볼 수 있다 */
		@Test
		void 다른_사람_북마크_목록_조회() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId2)
				.targetProfileId(profileId1)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByTargetProfileId(findCond, pageable);

			//then
			assertThat(resultPage.getContent()).hasSize(1)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::getOpenType)
				.containsExactly(tuple(bookmarkId1, ALL));
		}

		/* 다른 사람과 팔로우/팔로이 관계기 때문에 공개 범위가 all, partial 인 글을 볼 수 있다 */
		@Test
		void 팔로워_팔로이_관계인_사람의_북마크_목록_조회() {
			//given
			팔로우(profileId2, profileId1);
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId2)
				.targetProfileId(profileId1)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByTargetProfileId(findCond, pageable);

			//then
			assertThat(resultPage.getContent()).hasSize(2)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::getOpenType)
				.containsExactly(tuple(bookmarkId2, PARTIAL), tuple(bookmarkId1, ALL));
		}

		@Test
		void 내_북마크_목록_조회() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.targetProfileId(profileId1)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByTargetProfileId(findCond, pageable);

			//then
			assertThat(resultPage.getContent()).hasSize(3)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::getOpenType)
				.containsExactly(
					tuple(bookmarkId3, PRIVATE),
					tuple(bookmarkId2, PARTIAL),
					tuple(bookmarkId1, ALL)
				);
		}

		@Test
		void 다른_사람_북마크_즐겨찾기_후_조회() {
			//given
			즐겨찾기(profileId2, bookmarkId1);

			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId2)
				.targetProfileId(profileId1)
				.title("1")
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByTargetProfileId(findCond, pageable);

			assertThat(resultPage.getContent()).hasSize(1)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::isWriter, GetBookmarksResult::isFavorite)
				.containsExactly(tuple(bookmarkId1, false, true));
		}
	}

	@Nested
	class 피드_북마크_조회 {

		private long profileId1;
		private long profileId2;
		private long profileId3;

		private long bookmarkId4;
		private long bookmarkId5;
		private long bookmarkId6;

		private long bookmarkId7;
		private long bookmarkId8;

		private long bookmarkId10;

		//  사용자 1 			-팔로우->	사용자 2 				사용자 3
		// bookmark4 all,    		bookmark7 all, 		bookmark10 all
		// bookmark5 partial,		bookmark8 partial	bookmark11 partial
		// bookmark6 private,       bookmark9 private   bookmark12 private
		@BeforeEach
		void setUp() {
			profileId1 = 사용자_프로필_동시_등록("user1@gmail.com", GOOGLE, "user1", IT);
			profileId2 = 사용자_프로필_동시_등록("user2@gmail.com", GOOGLE, "user2", IT);
			profileId3 = 사용자_프로필_동시_등록("user3@gmail.com", GOOGLE, "user3", IT);

			// 링크 메타 데이터 3개 셋업
			북마크_등록(profileId3, "www.github.com", PRIVATE);
			북마크_등록(profileId3, "www.kakao.com", PRIVATE);
			bookmarkId10 = 북마크_등록(profileId3, "www.naver.com", ALL);

			북마크_등록(profileId2, "www.github.com", PRIVATE);
			bookmarkId8 = 북마크_등록(profileId2, "www.kakao.com", PARTIAL);
			bookmarkId7 = 북마크_등록(profileId2, "www.naver.com", ALL);

			bookmarkId6 = 북마크_등록(profileId1, "www.github.com", PRIVATE);
			bookmarkId5 = 북마크_등록(profileId1, "www.kakao.com", PARTIAL);
			bookmarkId4 = 북마크_등록(profileId1, "www.naver.com", ALL);

			팔로우(profileId1, profileId2);
		}

		@Test
		void 피드_북마크_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetFeedBookmarksResult> bookmarkPage = bookmarkService.getFeedBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(6);
			assertThat(bookmarkPage)
				.extracting(GetFeedBookmarksResult::getId, GetFeedBookmarksResult::isWriter)
				.containsExactly(
					tuple(bookmarkId4, true),
					tuple(bookmarkId5, true),
					tuple(bookmarkId6, true),

					tuple(bookmarkId7, false),
					tuple(bookmarkId8, false),

					tuple(bookmarkId10, false)
				);
			assertThat(bookmarkPage)
				.extracting(GetFeedBookmarksResult::getProfile)
				.extracting(GetFeedBookmarksResult.ProfileResult::getProfileId,
					GetFeedBookmarksResult.ProfileResult::isFollow)
				.containsExactly(
					tuple(profileId1, false),
					tuple(profileId1, false),
					tuple(profileId1, false),

					tuple(profileId2, true),
					tuple(profileId2, true),

					tuple(profileId3, false)
				);
		}

		@Test
		void 피드_북마크_조회_팔로우_여부로_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.follow(true)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<GetFeedBookmarksResult> bookmarkPage = bookmarkService.getFeedBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent())
				.extracting(GetFeedBookmarksResult::getId, GetFeedBookmarksResult::isWriter)
				.containsExactly(
					tuple(bookmarkId7, false),
					tuple(bookmarkId8, false)
				);
			assertThat(bookmarkPage.getContent())
				.extracting(GetFeedBookmarksResult::getProfile)
				.extracting(GetFeedBookmarksResult.ProfileResult::getProfileId,
					GetFeedBookmarksResult.ProfileResult::isFollow)
				.containsExactly(
					tuple(profileId2, true),
					tuple(profileId2, true)
				);
		}
	}

	@Test
	void 중복_url_확인_성공() {
		//given
		final long profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
		final long bookmarkId = 북마크_등록(profileId, "www.google.com");

		//when
		final Optional<Long> duplicated = bookmarkService.getBookmarkIdIfExist(profileId, "www.google.com");
		final Optional<Long> notDuplicated = bookmarkService.getBookmarkIdIfExist(profileId, "www.does.not.exist");

		//then
		assertThat(duplicated).isPresent().get().isEqualTo(bookmarkId);
		assertThat(notDuplicated).isEmpty();
	}

	@Test
	void 태그_목록_조회_성공() {
		//given
		final long profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);

		북마크_등록(profileId, "www.naver.com", "tag1", "tag2", "tag3");
		북마크_등록(profileId, "www.google.com", "tag1", "tag2");
		북마크_등록(profileId, "www.prgrms.com", "tag1");

		//when
		final List<GetUsedTagWithCountResult> result = bookmarkService.getUsedTagsWithCount(profileId);

		//then
		assertThat(result).hasSize(3)
			.extracting(GetUsedTagWithCountResult::getTag, GetUsedTagWithCountResult::getCount)
			.containsExactly(
				tuple("tag1", 3L),
				tuple("tag2", 2L),
				tuple("tag3", 1L)
			);
	}

}
