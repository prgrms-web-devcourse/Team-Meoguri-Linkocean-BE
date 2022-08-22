package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class ReactionRepositoryTest extends BasePersistenceTest {

	@Autowired
	private ReactionRepository reactionRepository;

	private Profile profile;
	private long profileId;

	private Bookmark bookmark;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		profile = 사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, "haha", IT, ART);
		profileId = profile.getId();

		final LinkMetadata linkMetadata = 링크_메타데이터_저장("www.google.com", "구글", "google.png");
		bookmark = 북마크_저장(profile, linkMetadata, "www.google.com");
		bookmarkId = bookmark.getId();
	}

	@Test
	void deleteByProfile_idAndBookmark_id_성공() {
		//given
		final Reaction reaction = reactionRepository.save(new Reaction(profile, bookmark, LIKE));

		//when
		reactionRepository.deleteByProfile_idAndBookmark_id(profileId, bookmarkId);

		//then
		assertThat(reactionRepository.findById(reaction.getId())).isEmpty();
	}

	@Test
	void 리액션_저장_실패_중복_요청() {
		//given
		reactionRepository.save(new Reaction(profile, bookmark, LIKE));

		//when then
		assertThatDataIntegrityViolationException()
			.isThrownBy(() -> reactionRepository.save(new Reaction(profile, bookmark, HATE)));
	}

	@Test
	void 북마크의_리액션_별_카운트_조회_성공() {
		//given
		final Profile anotherProfile = 사용자_프로필_동시_저장("papa@gmail.com", GOOGLE, "papa", IT);

		reactionRepository.save(new Reaction(profile, bookmark, LIKE));
		reactionRepository.save(new Reaction(anotherProfile, bookmark, HATE));

		//when
		final Map<ReactionType, Long> group = reactionRepository.countReactionGroup(bookmark);

		//then
		assertThat(group.getOrDefault(LIKE, 0L)).isEqualTo(1);
		assertThat(group.getOrDefault(HATE, 0L)).isEqualTo(1);
	}

}
