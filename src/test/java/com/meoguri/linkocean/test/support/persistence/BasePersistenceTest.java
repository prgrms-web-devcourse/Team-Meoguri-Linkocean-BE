package com.meoguri.linkocean.test.support.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
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

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class BasePersistenceTest {

	@Autowired
	protected EntityManager em;

	@Autowired
	protected EntityManagerFactory emf;

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

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private ReactionRepository reactionRepository;

	protected boolean isLoaded(final Object entity) {
		return emf.getPersistenceUnitUtil().isLoaded(entity);
	}

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

	protected Profile 사용자_프로필_동시_저장_등록(
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

	protected Bookmark 북마크_저장(final Profile writer, final LinkMetadata linkMetadata, final String url) {
		return 북마크_저장(writer, linkMetadata, null, null, ALL, null, url);
	}

	protected Bookmark 북마크_저장(final Profile writer, final LinkMetadata linkMetadata, final OpenType openType,
		final String url) {
		return 북마크_저장(writer, linkMetadata, null, null, openType, null, url);
	}

	protected Bookmark 북마크_저장(
		final Profile writer,
		final LinkMetadata linkMetadata,
		final String title,
		final String memo,
		final OpenType openType,
		final Category category,
		final String url,
		final Tag... tags
	) {
		return bookmarkRepository.save(new Bookmark(writer,
			linkMetadata,
			title,
			memo,
			openType,
			category,
			url,
			Arrays.stream(tags).collect(toList())
		));
	}

	protected Bookmark 북마크_링크_메타데이터_동시_저장(
		final Profile writer,
		final String url
	) {
		return 북마크_링크_메타데이터_동시_저장(writer, null, url);
	}

	protected Bookmark 북마크_링크_메타데이터_동시_저장(
		final Profile writer,
		final Category category,
		final String url
	) {
		return 북마크_저장(writer, 링크_메타데이터_저장(url, "제목 없음", "default-image.png"), "title", "memo", ALL, category, url);
	}

	protected Bookmark 북마크_링크_메타데이터_동시_저장(
		final Profile writer,
		final String title,
		final OpenType openType,
		final Category category,
		final String url,
		final Tag... tags
	) {
		return 북마크_저장(writer, 링크_메타데이터_저장(url, "제목 없음", "default-image.png"), title, "memo", openType, category, url,
			tags);
	}

	/* em.flush & em.clear occurs */
	protected void 좋아요_저장(final Profile profile, final Bookmark bookmark) {
		reactionRepository.save(new Reaction(profile, bookmark, LIKE));
		bookmarkRepository.addLikeCount(bookmark.getId());
	}

	protected void 싫어요_저장(final Profile profile, final Bookmark bookmark) {
		reactionRepository.save(new Reaction(profile, bookmark, HATE));
	}

	protected void 즐겨찾기_저장(final Profile profile, final Bookmark bookmark) {
		profile.favorite(bookmark);
	}

	protected Profile 프로필_조회(final long profileId) {
		return profileRepository.findById(profileId).orElseThrow();
	}

	protected Bookmark 북마크_조회(final long bookmarkId) {
		return bookmarkRepository.findById(bookmarkId).orElseThrow();
	}
}
