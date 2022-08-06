package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class TagServiceImplTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private TagService tagService;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;

	private long userId;
	private Profile profile;
	private LinkMetadata link;
	private Tag tag1;
	private Tag tag2;
	private Tag tag3;

	@BeforeEach
	void setUp() {
		// 프로필, 링크 셋업
		User user = userRepository.save(createUser());
		profile = profileRepository.save(createProfile(user));
		link = linkMetadataRepository.save(createLinkMetadata());

		userId = user.getId();

		// 태그 셋업
		tag1 = tagRepository.save(new Tag("tag1"));
		tag2 = tagRepository.save(new Tag("tag2"));
		tag3 = tagRepository.save(new Tag("tag3"));
	}

	@Test
	void 태그_목록_조회_성공() {
		//given
		final Bookmark bookmark1 = createBookmark(profile, link, "bookmark1", "인문", "www.naver.com");
		final Bookmark bookmark2 = createBookmark(profile, link, "bookmark2", "인문", "www.google.com");
		final Bookmark bookmark3 = createBookmark(profile, link, "bookmark3", "인문", "www.prgrms.com");

		bookmark1.addBookmarkTag(tag1);
		bookmark1.addBookmarkTag(tag2);
		bookmark1.addBookmarkTag(tag3);

		bookmark2.addBookmarkTag(tag2);
		bookmark2.addBookmarkTag(tag3);

		bookmark3.addBookmarkTag(tag3);

		bookmarkRepository.save(bookmark1);
		bookmarkRepository.save(bookmark2);
		bookmarkRepository.save(bookmark3);

		em.flush();
		em.clear();

		//when
		final List<GetProfileTagsResult> result = tagService.getMyTags(userId);

		//then
		assertThat(result).hasSize(3)
			.extracting(GetProfileTagsResult::getTag, GetProfileTagsResult::getCount)
			.containsExactly(
				tuple("tag3", 3),
				tuple("tag2", 2),
				tuple("tag1", 1)
			);
	}
}
