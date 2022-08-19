package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

@Import(ReactionQuery.class)
class ReactionQueryTest extends BasePersistenceTest {

	@Autowired
	private ReactionQuery reactionQuery;

	private Profile profile;
	private Bookmark bookmark;

	@BeforeEach
	void setUp() {
		profile = 사용자_프로필_저장_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);
		bookmark = 북마크_링크_메타데이터_저장(profile, "google.com");
	}

	@Test
	void 북마크의_리액션별_카운트_조회_성공() {
		//given
		final Profile anotherProfile = 사용자_프로필_저장_등록("papa@gmail.com", GOOGLE, "papa", IT);
		좋아요_저장(profile, bookmark);
		싫어요_저장(anotherProfile, bookmark);

		//when
		final Map<ReactionType, Long> reactionCountMap = reactionQuery.getReactionCountMap(bookmark);

		//then
		assertThat(reactionCountMap.get(LIKE)).isEqualTo(1);
		assertThat(reactionCountMap.get(HATE)).isEqualTo(1);
	}

	@Test
	void 리액션_여부_맵을_조회할_수_있다() {
		//given
		좋아요_저장(profile, bookmark);

		//when
		final Map<ReactionType, Boolean> reactionMap = reactionQuery.getReactionMap(profile.getId(), bookmark);

		//then
		assertThat(reactionMap.get(LIKE)).isTrue();
		assertThat(reactionMap.get(HATE)).isFalse();
	}
}
