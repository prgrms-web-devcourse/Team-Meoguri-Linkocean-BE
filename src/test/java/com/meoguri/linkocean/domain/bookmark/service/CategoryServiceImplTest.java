package com.meoguri.linkocean.domain.bookmark.service;

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

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
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

	@BeforeEach
	void setUp() {
		// 유저, 프로필, 링크 셋업
		user = userRepository.save(createUser());
		profile = profileRepository.save(createProfile(user));
		link = linkMetadataRepository.save(createLinkMetadata());
	}

	@Test
	void 사용자가_작성한_북마크가있는_카테고리_조회_성공() {
		//given
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.naver.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.prgrms.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.daum.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.SOCIAL, "www.hello.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.SOCIAL, "www.linkocean.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.HEALTH, "www.jacob.com"));

		//when
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		//then
		assertThat(categories).contains("IT", "사회", "건강");
	}

	@Test
	void 사용자가_작성한_북마크가있는_카테고리_조회_카테고리가_null_일때() {
		//given
		bookmarkRepository.save(createBookmark(profile, link, "제목", null, "www.naver.com"));

		//when
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		//then
		assertThat(categories).isEmpty();
	}

}
