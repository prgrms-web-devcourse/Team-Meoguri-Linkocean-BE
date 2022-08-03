package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark.Category;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
class CategoryServiceImplTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryService categoryService;

	private User user;
	private Profile profile;
	private LinkMetadata link;
	private Tag tag1;
	private Tag tag2;
	private Tag tag3;

	@BeforeEach
	void setUp() {
		// 유저, 프로필, 링크 셋업
		user = userRepository.save(createUser());
		profile = profileRepository.save(createProfile(user));
		link = linkMetadataRepository.save(createLinkMetadata());
	}

	@Test
	void 카테고리_전체조회_성공() {
		//when
		final List<String> allCategories = categoryService.getAllCategories();

		//then
		final List<String> expectedCategories
			= Arrays.stream(Category.values()).map(Category::getKorName).collect(toList());
		assertThat(allCategories)
			.containsExactlyElementsOf(expectedCategories);
	}

	@Test
	void 사용자가_작성한_북마크가있는_카테고리_조회_성공() {
		//given
		bookmarkRepository.save(createBookmark(profile, link, "제목", "인문", "www.naver.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", "인문", "www.prgrms.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", "인문", "www.daum.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", "사회", "www.hello.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", "사회", "www.linkocean.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", "과학", "www.jacob.com"));

		//when
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		//then
		assertThat(categories).contains("인문", "사회", "과학");
	}

	@Test
	void 사용자가_작성한_북마크가있는_카테고리_조회_카테고리가_null일때() {
		//given
		bookmarkRepository.save(createBookmark(profile, link, "제목", null, "www.naver.com"));

		//when
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		//then
		assertThat(categories).isEmpty();
	}

}
