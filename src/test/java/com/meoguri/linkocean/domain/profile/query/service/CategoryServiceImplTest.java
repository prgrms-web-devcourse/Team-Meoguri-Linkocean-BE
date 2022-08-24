package com.meoguri.linkocean.domain.profile.query.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class CategoryServiceImplTest extends BaseServiceTest {

	@Autowired
	private CategoryService categoryService;

	private long profileId;

	@BeforeEach
	void setUp() {
		// 유저, 프로필, 링크 셋업
		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
	}

	@Test
	void 사용자가_작성한_북마크가있는_카테고리_조회_성공() {
		//given
		북마크_등록(profileId, "www.naver.com", IT);
		북마크_등록(profileId, "www.prgrms.com", IT);
		북마크_등록(profileId, "www.daum.com", IT);
		북마크_등록(profileId, "www.hello.com", SOCIAL);
		북마크_등록(profileId, "www.linkocean.com", SOCIAL);
		북마크_등록(profileId, "www.jacob.com", HEALTH);

		//when
		final List<Category> categories = categoryService.getUsedCategories(profileId);

		//then
		assertThat(categories).contains(IT, SOCIAL, HEALTH);
	}
}
