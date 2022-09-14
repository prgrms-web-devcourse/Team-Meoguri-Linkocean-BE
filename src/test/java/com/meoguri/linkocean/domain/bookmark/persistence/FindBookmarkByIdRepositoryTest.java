package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class FindBookmarkByIdRepositoryTest extends BasePersistenceTest {

	@Autowired
	private FindBookmarkByIdRepository findBookmarkByIdRepository;

	private Profile writer;

	private LinkMetadata linkMetadata;

	@BeforeEach
	void setUp() {
		// 프로필, 링크 셋업
		writer = 사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, "haha", IT);
		linkMetadata = 링크_메타데이터_저장("www.google.com", "구글", "google.png");
	}

	@Test
	void 아이디로_전체_페치_조회_성공() {
		//given
		final Long tagId = 태그_저장("tag1").getId();
		final Bookmark savedBookmark = 북마크_저장(writer, linkMetadata, "title", "memo", ALL, IT, "www.google.com", tagId);

		//when
		final Bookmark findBookmark = findBookmarkByIdRepository.findByIdFetchAll(savedBookmark.getId());

		//then
		assertAll(
			() -> assertThat(isLoaded(findBookmark.getWriter())).isEqualTo(true),
			() -> assertThat(findBookmark.getTagIds()).containsExactly(tagId)
		);
	}

}
