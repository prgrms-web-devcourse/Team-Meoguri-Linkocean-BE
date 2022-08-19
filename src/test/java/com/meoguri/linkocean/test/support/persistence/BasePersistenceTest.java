package com.meoguri.linkocean.test.support.persistence;

import static com.meoguri.linkocean.test.support.common.Fixture.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Transactional
@DataJpaTest
public class BasePersistenceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private TagRepository tagRepository;

	protected User 사용자_저장(final String email, final OAuthType oAuthType) {
		return userRepository.save(createUser(email, oAuthType));
	}

	protected Profile 프로필_저장(final String username, final Category... categories) {
		return profileRepository.save(createProfile(username, categories));
	}

	protected Profile 프로필_등록(final User user, final Profile profile) {
		user.registerProfile(profile);
		return profile;
	}

	protected Profile 프로필_저장_등록(final User user, final String username, final Category... categories) {
		return 프로필_등록(user, 프로필_저장(username, categories));
	}

	protected Profile 사용자_프로필_저장_등록(
		final String email,
		final OAuthType oAuthType,
		final String username,
		final Category... categories
	) {
		return 프로필_저장_등록(사용자_저장(email, oAuthType), username, categories);
	}

	protected void 팔로우_저장(final Profile follower, final Profile followee) {
		followRepository.save(new Follow(follower, followee));
	}

	protected LinkMetadata 링크_메타데이터_저장(final String link, final String title, final String image) {
		return linkMetadataRepository.save(new LinkMetadata(link, title, image));
	}

	protected Tag 태그_저장(final String name) {
		return tagRepository.save(new Tag(name));
	}
}
