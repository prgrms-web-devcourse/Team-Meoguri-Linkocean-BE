package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Url;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

@SpringBootTest
@Transactional
class BookmarkServiceImplTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private User user;
	private Profile profile;
	private LinkMetadata linkMetadata;

	@BeforeEach
	void setUp() {
		user = userRepository.save(createUser());
		profile = profileRepository.save(createProfile(user));
		linkMetadata = linkMetadataRepository.save(createLinkMetadata());
	}

	@Test
	void 북마크_등록_성공() {
		//given
		final RegisterBookmarkCommand registerBookmarkCommand = new RegisterBookmarkCommand(
			user.getId(),
			Url.toString(linkMetadata.getUrl()),
			"title",
			"memo",
			"it",
			List.of("tag1", "tag2"),
			"all"
		);

		//when
		final long savedBookmarkId = bookmarkService.registerBookmark(registerBookmarkCommand);

		//then

		entityManager.flush();
		entityManager.clear();

		final Bookmark retrievedBookmark = bookmarkRepository.findById(savedBookmarkId).get();
		assertThat(retrievedBookmark).isNotNull()
			.extracting(
				Bookmark::getProfile,
				Bookmark::getLinkMetadata,
				Bookmark::getTitle,
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(
				profile,
				linkMetadata,
				registerBookmarkCommand.getTitle(),
				registerBookmarkCommand.getMemo(),
				registerBookmarkCommand.getCategory(),
				registerBookmarkCommand.getOpenType()
			);

		final List<String> tagNameList = retrievedBookmark.getBookmarkTags().stream()
			.map(bookmarkTag -> bookmarkTag.getTag().getName())
			.collect(Collectors.toList());

		assertThat(tagNameList)
			.containsExactlyInAnyOrder(
				registerBookmarkCommand.getTagNames().get(0),
				registerBookmarkCommand.getTagNames().get(1)
			);
	}

	@Test
	void 북마크_생성_실패_유효하지_않은_사용자() {
		//given
		final long invalidId = 10L;
		final RegisterBookmarkCommand registerBookmarkCommand = new RegisterBookmarkCommand(
			invalidId,
			Url.toString(linkMetadata.getUrl()),
			"title",
			"memo",
			"it",
			List.of("tag1", "tag2"),
			"all"
		);

		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> bookmarkService.registerBookmark(registerBookmarkCommand));
	}

	@Test
	void 북마크_생성_실패_유효하지_않은_url() {
		//given
		final String invalidUrl = "www.invalid.com";
		final RegisterBookmarkCommand registerBookmarkCommand = new RegisterBookmarkCommand(
			user.getId(),
			invalidUrl,
			"title",
			"memo",
			"it",
			List.of("tag1", "tag2"),
			"all"
		);

		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> bookmarkService.registerBookmark(registerBookmarkCommand));
	}

	@Test
	void 중복_url_북마크_생성_요청에_따라_실패() {
		//given
		final Bookmark bookmark = Bookmark.builder()
			.profile(profile)
			.linkMetadata(linkMetadata)
			.title("title")
			.memo("memo")
			.category("it")
			.openType("all")
			.build();
		bookmarkRepository.save(bookmark);

		/* 중복 url 생성 요청 */
		final RegisterBookmarkCommand registerBookmarkCommand = new RegisterBookmarkCommand(
			user.getId(),
			Url.toString(linkMetadata.getUrl()),
			"title",
			"memo",
			"it",
			List.of("tag1", "tag2"),
			"all"
		);

		//when then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> bookmarkService.registerBookmark(registerBookmarkCommand));
	}
}