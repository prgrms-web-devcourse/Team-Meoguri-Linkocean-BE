package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class BookmarkRepositoryTest extends BasePersistenceTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private Profile writer;
	private long writerId;

	private LinkMetadata linkMetadata;

	private long tagId1;
	private long tagId2;
	private long tagId3;

	@BeforeEach
	void setUp() {
		// 프로필, 링크 셋업
		writer = 사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, "haha", IT);
		writerId = writer.getId();

		linkMetadata = 링크_메타데이터_저장("www.google.com", "구글", "google.png");

		// 태그 셋업
		tagId1 = 태그_저장("tag1").getId();
		tagId2 = 태그_저장("tag2").getId();
		tagId3 = 태그_저장("tag3").getId();
	}

	@Test
	void 아이디와_작성자로_조회_성공() {
		//given
		final Bookmark savedBookmark = 북마크_저장(writer, linkMetadata, "www.google.com");

		//when
		final Optional<Bookmark> oFoundBookmark =
			bookmarkRepository.findByIdAndWriterId(savedBookmark.getId(), writerId);

		//then
		assertThat(oFoundBookmark).isPresent().get().isEqualTo(savedBookmark);
	}

	@Test
	void 아이디로_전체_페치_조회_성공() {
		//given
		final Bookmark savedBookmark = 북마크_저장(writer, linkMetadata, "title", "memo", ALL, IT, "www.google.com", tagId1);

		//when
		final Optional<Bookmark> oFindBookmark = bookmarkRepository.findByIdFetchAll(savedBookmark.getId());

		//then
		assertAll(
			() -> assertThat(oFindBookmark).isPresent(),
			() -> assertThat(isLoaded(oFindBookmark.get().getWriter())).isEqualTo(true),
			() -> assertThat(oFindBookmark.get().getTagIds()).containsExactly(tagId1)
		);
	}

	@Test
	void 게시글이_존재하는_카테고리이름_반환() {
		//given
		북마크_링크_메타데이터_동시_저장(writer, IT, "www.youtube.com");
		북마크_링크_메타데이터_동시_저장(writer, IT, "www.naver.com");
		북마크_링크_메타데이터_동시_저장(writer, IT, "www.prgrms.com");
		북마크_링크_메타데이터_동시_저장(writer, SOCIAL, "www.daum.com");
		북마크_링크_메타데이터_동시_저장(writer, SOCIAL, "www.hello.com");
		북마크_링크_메타데이터_동시_저장(writer, SCIENCE, "www.linkocean.com");

		//when
		final List<Category> categories = bookmarkRepository.findCategoryExistsBookmark(writerId);

		//then
		assertThat(categories).contains(IT, SOCIAL, SCIENCE);
	}

	@Test
	void findIdByWriterIdAndUrl_성공() {
		//given
		final Bookmark bookmark = 북마크_링크_메타데이터_동시_저장(writer, "www.youtube.com");

		//when
		final Optional<Long> oBookmarkId1 = bookmarkRepository.findIdByWriterIdAndUrl(writerId, "www.youtube.com");
		final Optional<Long> oBookmarkId2 = bookmarkRepository.findIdByWriterIdAndUrl(writerId, "www.not-exists.com");

		//then
		assertThat(oBookmarkId1).isPresent().get().isEqualTo(bookmark.getId());
		assertThat(oBookmarkId2).isEmpty();
	}

	@ParameterizedTest
	@CsvSource(value = {
		"www.youtube.com,     null,  LIKE, 1, 0",
		"www.haha.com,        LIKE,  LIKE, 0, 0",
		"www.naver.com, 	  HATE,  LIKE, 1, 0",
		"www.prgrms.com,      null,  HATE, 0, 1",
		"www.linkocean.com,   LIKE,  HATE, 0, 1",
		"www.columbia.com,    HATE,  HATE, 0, 0",
	}, nullValues = "null")
	void 북마크_LikeCount_업데이트_성공(
		final String url,
		final ReactionType existedType,
		final ReactionType requestType,
		final long expectedLikeCount,
		final long expectedHateCount
	) {
		//given
		final Bookmark bookmark = 북마크_링크_메타데이터_동시_저장(writer, url);
		writer = 리액션_요청(writer, bookmark, existedType);
		bookmark.requestReaction(writerId, requestType);

		//when
		bookmarkRepository.updateLikeCount(bookmark.getId(), existedType, requestType);

		//then
		assertThat(bookmark.countReactionGroup())
			.containsAllEntriesOf(Map.of(LIKE, expectedLikeCount, HATE, expectedHateCount));
	}
}
