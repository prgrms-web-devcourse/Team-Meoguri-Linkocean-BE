package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class BookmarkRepositoryTest extends BasePersistenceTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private Profile writer;
	private long writerId;

	private LinkMetadata linkMetadata;

	private Tag tag1;
	private Tag tag2;
	private Tag tag3;

	@BeforeEach
	void setUp() {
		// 프로필, 링크 셋업
		writer = 사용자_프로필_동시_저장_등록("haha@gmail.com", GOOGLE, "haha", IT);
		writerId = writer.getId();

		linkMetadata = 링크_메타데이터_저장("www.google.com", "구글", "google.png");

		// 태그 셋업
		tag1 = 태그_저장("tag1");
		tag2 = 태그_저장("tag2");
		tag3 = 태그_저장("tag3");
	}

	@Test
	void existsByWriterAndLinkMetadata_성공() {
		//given
		북마크_저장(writer, linkMetadata, "www.google.com");

		//when
		final boolean exists =
			bookmarkRepository.existsByWriterAndLinkMetadata(writer, linkMetadata);

		//then
		assertThat(exists).isEqualTo(true);
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
	void 작성자의_아이디로_태그페치_조회_성공() {
		//given
		북마크_저장(writer, linkMetadata, "bookmark1", "memo1", ALL, IT, "www.naver.com", tag1, tag2, tag3);
		북마크_저장(writer, linkMetadata, "bookmark2", "memo2", ALL, IT, "www.naver.com", tag2, tag3);
		북마크_저장(writer, linkMetadata, "bookmark3", "memo3", ALL, IT, "www.naver.com", tag3);

		//when
		final List<Bookmark> bookmarks = bookmarkRepository.findByWriterIdFetchTags(writer.getId());

		//then
		assertThat(bookmarks).hasSize(3)
			.extracting(Bookmark::getTitle, Bookmark::getTagNames)
			.containsExactly(
				tuple("bookmark1", List.of("tag1", "tag2", "tag3")),
				tuple("bookmark2", List.of("tag2", "tag3")),
				tuple("bookmark3", List.of("tag3"))
			);

		assertThat(bookmarks.get(0).getWriter().getUsername()).isEqualTo("haha");
		assertThat(bookmarks.get(0).getLinkMetadata().getTitle()).isEqualTo("구글");

	}

	@Test
	void 아이디로_전체_페치_조회_성공() {
		//given
		final Bookmark savedBookmark = 북마크_저장(writer, linkMetadata, "title", "memo", ALL, IT, "www.google.com", tag1);

		//when
		final Optional<Bookmark> oFindBookmark = bookmarkRepository.findByIdFetchAll(savedBookmark.getId());

		//then
		assertAll(
			() -> assertThat(oFindBookmark).isPresent(),
			() -> assertThat(isLoaded(oFindBookmark.get().getWriter())).isEqualTo(true),
			() -> assertThat(isLoaded(oFindBookmark.get().getLinkMetadata())).isEqualTo(true),
			() -> assertThat(oFindBookmark.get().getTagNames()).contains(tag1.getName())
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

	@Test
	void 북마크_LikeCount_증가_성공() {
		//given
		final Bookmark bookmark = 북마크_링크_메타데이터_동시_저장(writer, "www.youtube.com");

		//when
		bookmarkRepository.addLikeCount(bookmark.getId());

		//then
		final Optional<Bookmark> oFoundBookmark = bookmarkRepository.findById(bookmark.getId());
		assertThat(oFoundBookmark.get().getLikeCount()).isEqualTo(1);
	}
}
