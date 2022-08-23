package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.profile.service.query.dto.GetProfileTagsResult;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class TagServiceImplTest extends BaseServiceTest {

	@Autowired
	private TagService tagService;

	private long profileId;

	@BeforeEach
	void setUp() {
		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
	}

	@Test
	void 태그_목록_조회_성공() {
		//given
		북마크_등록(profileId, "www.naver.com", "tag1", "tag2", "tag3");
		북마크_등록(profileId, "www.google.com", "tag1", "tag2");
		북마크_등록(profileId, "www.prgrms.com", "tag1");

		//when
		final List<GetProfileTagsResult> result = tagService.getTags(profileId);

		//then
		assertThat(result).hasSize(3)
			.extracting(GetProfileTagsResult::getTag, GetProfileTagsResult::getCount)
			.containsExactly(
				tuple("tag1", 3),
				tuple("tag2", 2),
				tuple("tag3", 1)
			);
	}
}
