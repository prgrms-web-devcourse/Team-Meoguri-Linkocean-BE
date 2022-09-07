package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindUsedTagIdWithCountResult;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class CustomBookmarkRepositoryImplTest extends BasePersistenceTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private Profile profile;
	private long profileId;

	private Bookmark bookmark1;
	private Bookmark bookmark2;
	private Bookmark bookmark3;

	private long bookmarkId1;
	private long bookmarkId2;
	private long bookmarkId3;

	private long tagId1;
	private long tagId2;

	void setUpInternal() {
		// 사용자 1명 셋업 - 크러쉬
		profile = 사용자_프로필_동시_저장("crush@gmail.com", NAVER, "crush", IT);
		profileId = profile.getId();

		// 태그 두개 셋업
		tagId1 = 태그_저장("tag1").getId();
		tagId2 = 태그_저장("tag2").getId();

		bookmark1 = 북마크_링크_메타데이터_동시_저장(profile, "title1", ALL, IT, "www.naver.com", tagId1, tagId2);
		bookmark2 = 북마크_링크_메타데이터_동시_저장(profile, "title2", PARTIAL, HOME, "www.google.com", tagId1);
		bookmark3 = 북마크_링크_메타데이터_동시_저장(profile, "title3", PRIVATE, IT, "www.github.com");

		즐겨찾기_저장(profile, bookmark1);
		즐겨찾기_저장(profile, bookmark2);

		profile = 좋아요_저장(profile, bookmark1);
		profile = 싫어요_저장(profile, bookmark3);

		bookmarkId1 = bookmark1.getId();
		bookmarkId2 = bookmark2.getId();
		bookmarkId3 = bookmark3.getId();
	}

	@Test
	void existsByWriterAndUrl_성공() {
		//given
		profile = 사용자_프로필_동시_저장("crush@gmail.com", NAVER, "crush", IT);
		final LinkMetadata linkMetadata = 링크_메타데이터_저장("www.google.com", "구글", "google.png");
		북마크_저장(profile, linkMetadata, "www.google.com");

		//when
		final boolean exist = bookmarkRepository.existsByWriterAndUrl(profile, "www.google.com");

		//then
		assertThat(exist).isTrue();
	}

	@Nested
	class 북마크_카테고리로_조회 {

		@BeforeEach
		void setUp() {
			setUpInternal();
		}

		@Test
		void 북마크_카테고리로_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.category(IT)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId3, IT),
					tuple(bookmarkId1, IT)
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_필터링_좋아요_정렬() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.category(IT)
				.build();
			final Pageable pageable = createPageable("like");

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId1, IT),
					tuple(bookmarkId3, IT)
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_제목으로_필터링() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.category(IT)
				.title("1")
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
				.containsExactly(
					tuple(bookmarkId1, IT, "title1")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_즐겨찾기_조회 {

		@BeforeEach
		void setUp() {
			setUpInternal();
		}

		@Test
		void 북마크_즐겨찾기_조회_제목으로_필터링_성공() {
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profileId)
				.title("1")
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1L);
		}

		@Test
		void 북마크_즐겨찾기_조회_좋아요_순으로_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profileId)
				.favorite(true)
				.build();
			final Pageable pageable = createPageable("like");

			// when
			final Page<Bookmark> bookmarks = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId2);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_즐겨찾기_다른사람의_글도_있는경우() {
			//setup
			final Profile profile2 = 사용자_프로필_동시_저장("user2@naver.com", NAVER, "user2", IT);
			final Bookmark bookmark4 = 북마크_링크_메타데이터_동시_저장(profile2, "www.linkocean.com");
			final Bookmark bookmark5 = 북마크_링크_메타데이터_동시_저장(profile2, "www.artzip.com");

			즐겨찾기_저장(profile, bookmark4);
			즐겨찾기_저장(profile2, bookmark3);
			즐겨찾기_저장(profile2, bookmark5);
			final Pageable pageable = createPageable();

			//user1 -> user1
			//given
			final BookmarkFindCond findCond1 = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profileId)
				.favorite(true)
				.build();

			//when
			final Page<Bookmark> bookmarks1 = bookmarkRepository.findBookmarks(findCond1, pageable);

			//then
			assertThat(bookmarks1).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmark4.getId(), bookmarkId2, bookmarkId1);
			assertThat(bookmarks1.getTotalElements()).isEqualTo(3);

			//user1 -> user2
			//given
			final BookmarkFindCond findCond2 = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profile2.getId())
				.favorite(true)
				.build();

			//when
			final Page<Bookmark> bookmarks2 = bookmarkRepository.findBookmarks(findCond2, pageable);

			//then
			assertThat(bookmarks2).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmark5.getId(), bookmark3.getId());
			assertThat(bookmarks2.getTotalElements()).isEqualTo(2);

		}
	}

	@Nested
	class 북마크_태그로_조회 {

		@BeforeEach
		void setUp() {
			setUpInternal();
		}

		@Test
		void 북마크_태그로_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.tags(List.of("tag1"))
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);

			assertThat(bookmarkPage.getContent().get(0).getId()).isEqualTo(bookmarkId2);
			assertThat(bookmarkPage.getContent().get(0).getTagIds()).containsExactlyInAnyOrder(tagId1);

			assertThat(bookmarkPage.getContent().get(1).getId()).isEqualTo(bookmarkId1);
			assertThat(bookmarkPage.getContent().get(1).getTagIds()).containsExactlyInAnyOrder(tagId1, tagId2);
		}

		@Test
		void 북마크_태그로_조회_좋아요_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.tags(List.of("tag1"))
				.build();
			final Pageable pageable = createPageable("like");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);

			assertThat(bookmarkPage.getContent().get(0).getId()).isEqualTo(bookmarkId1);
			assertThat(bookmarkPage.getContent().get(0).getTagIds()).containsExactlyInAnyOrder(tagId1, tagId2);

			assertThat(bookmarkPage.getContent().get(1).getId()).isEqualTo(bookmarkId2);
			assertThat(bookmarkPage.getContent().get(1).getTagIds()).containsExactlyInAnyOrder(tagId1);
		}

		@Test
		void 북마크_태그로_조회_제목_필터링_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.tags(List.of("tag1"))
				.title("1")
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);

			assertThat(bookmarkPage.getContent().get(0).getId()).isEqualTo(bookmarkId1);
			assertThat(bookmarkPage.getContent().get(0).getTagIds()).containsExactlyInAnyOrder(tagId1, tagId2);
			assertThat(bookmarkPage.getContent().get(0).getTitle()).isEqualTo("title1");
		}
	}

	@Nested
	class 북마크_기본_조회 {

		@BeforeEach
		void setUp() {
			setUpInternal();
		}

		@Test
		void 북마크_기본_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId3, bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_Partial_공개범위_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.openType(PARTIAL)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_기본_조회_좋아요_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.build();
			final Pageable pageable = createPageable("like");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId3, bookmarkId2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_제목으로_필터링() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.targetProfileId(profileId)
				.title("1")
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}

		@Test
		void 북마크_조회_성공_페이징() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profileId)
				.build();
			final Pageable pageable = PageRequest.of(0, 2, Sort.by("upload"));

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent())
				.containsExactly(bookmark3, bookmark2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		/* 카운트 쿼리 최적화 확인 */
		@Test
		void 북마크_조회_성공_카테고리_페이징() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId)
				.targetProfileId(profileId)
				.category(IT)
				.build();
			final Pageable pageable = PageRequest.of(0, 2, Sort.by("upload"));

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent()).containsExactly(bookmark3, bookmark1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}
	}

	@Nested
	class 피드_북마크_조회 {

		private long profileId1;

		private Bookmark bookmark4;
		private Bookmark bookmark5;
		private Bookmark bookmark6;

		private Bookmark bookmark7;
		private Bookmark bookmark8;

		private Bookmark bookmark10;

		//  사용자 1 			-팔로우->	사용자 2 				사용자 3
		// bookmark4 all,    		bookmark7 all, 		bookmark10 all
		// bookmark5 partial,		bookmark8 partial	bookmark11 partial
		// bookmark6 private,       bookmark9 private   bookmark12 private
		@BeforeEach
		void setUp() {
			Profile profile1 = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
			Profile profile2 = 사용자_프로필_동시_저장("user2@gmail.com", GOOGLE, "user2", IT);
			Profile profile3 = 사용자_프로필_동시_저장("user3@gmail.com", GOOGLE, "user3", IT);

			profileId1 = profile1.getId();
			팔로우_저장(profile1, profile2);

			LinkMetadata github = 링크_메타데이터_저장("www.github.com", "깃헙", "github.png");
			LinkMetadata google = 링크_메타데이터_저장("www.google.com", "구글", "google.png");
			LinkMetadata naver = 링크_메타데이터_저장("www.naver.com", "네이버", "naver.png");

			북마크_저장(profile3, github, PRIVATE, "www.github.com");
			북마크_저장(profile3, google, PARTIAL, "www.github.com");
			bookmark10 = 북마크_저장(profile3, naver, ALL, "naver.com");

			북마크_저장(profile2, github, PRIVATE, "www.github.com");
			bookmark8 = 북마크_저장(profile2, google, PARTIAL, "github.com");
			bookmark7 = 북마크_저장(profile2, naver, ALL, "google.com");

			bookmark6 = 북마크_저장(profile1, github, PRIVATE, "naver.com");
			bookmark5 = 북마크_저장(profile1, google, PARTIAL, "github.com");
			bookmark4 = 북마크_저장(profile1, naver, ALL, "google.com");
		}

		@Test
		void 피드_북마크_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.build();
			final Pageable pageable = createPageable("upload");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(6);
			assertThat(bookmarkPage.getContent())
				.containsExactly(bookmark4, bookmark5, bookmark6, bookmark7, bookmark8, bookmark10);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(6);
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
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent())
				.containsExactly(bookmark7, bookmark8);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 피드_북마크_조회_성공_페이징() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.build();
			final Pageable pageable = PageRequest.of(0, 2, Sort.by("upload"));

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent()).containsExactly(bookmark4, bookmark5);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(6);
		}

	}

	@Test
	void 사용자의_사용태그_별_카운트_조회_성공() {
		//given
		final Profile profile1 = 사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, "haha", IT);

		tagId1 = 태그_저장("tag1").getId();
		tagId2 = 태그_저장("tag2").getId();

		북마크_링크_메타데이터_동시_저장(profile1, "구글", ALL, IT, "www.google.com", tagId1, tagId2);
		북마크_링크_메타데이터_동시_저장(profile1, "네이버", ALL, IT, "www.naver.com", tagId1);
		북마크_링크_메타데이터_동시_저장(profile1, "링크오션", ALL, IT, "www.linkocean.com");

		//when
		final List<FindUsedTagIdWithCountResult> result =
			pretty(() -> bookmarkRepository.findUsedTagIdsWithCount(profile1.getId()));

		//then
		assertThat(result)
			.extracting(FindUsedTagIdWithCountResult::getTagId, FindUsedTagIdWithCountResult::getCount)
			.containsExactlyInAnyOrder(
				tuple(tagId1, 2L),
				tuple(tagId2, 1L)
			);
	}
}
