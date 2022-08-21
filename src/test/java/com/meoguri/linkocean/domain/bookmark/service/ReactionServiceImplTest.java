package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class ReactionServiceImplTest extends BaseServiceTest {

	@Autowired
	private ReactionService reactionService;

	private long profileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
		bookmarkId = 북마크_등록(profileId, "www.youtube.com");
	}

	@Test
	void 리액션_요청_등록_성공() {
		//given
		final ReactionCommand reactionCommand = new ReactionCommand(profileId, bookmarkId, LIKE);

		//when
		reactionService.requestReaction(reactionCommand);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
		assertThat(result.getReaction().get(LIKE)).isEqualTo(true);
		assertThat(result.getReaction().get(HATE)).isEqualTo(false);
		assertThat(result.getReactionCount().get(LIKE)).isEqualTo(1);
		assertThat(result.getReactionCount().get(HATE)).isEqualTo(0);
	}

	@Test
	void 리액션_요청_취소_성공() {
		//given
		좋아요_요청(profileId, bookmarkId);

		//when
		좋아요_요청(profileId, bookmarkId);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
		assertThat(result.getReaction().get(LIKE)).isEqualTo(false);
		assertThat(result.getReaction().get(HATE)).isEqualTo(false);
		assertThat(result.getReactionCount().get(LIKE)).isEqualTo(0);
		assertThat(result.getReactionCount().get(HATE)).isEqualTo(0);
	}

	@Test
	void 리액션_요청_변경_성공() {
		//given
		좋아요_요청(profileId, bookmarkId);

		//when
		싫어요_요청(profileId, bookmarkId);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
		assertThat(result.getReaction().get(LIKE)).isEqualTo(false);
		assertThat(result.getReaction().get(HATE)).isEqualTo(true);
		assertThat(result.getReactionCount().get(LIKE)).isEqualTo(0);
		assertThat(result.getReactionCount().get(HATE)).isEqualTo(1);
	}
}
