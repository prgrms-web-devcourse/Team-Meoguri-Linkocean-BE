package com.meoguri.linkocean.test.support.domain.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.command.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.profile.command.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.tag.entity.Tag;
import com.meoguri.linkocean.domain.tag.persistence.TagRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@PersistenceTest
public abstract class BasePersistenceTest {

	@Autowired
	protected EntityManager em;

	@Autowired
	protected EntityManagerFactory emf;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	protected boolean isLoaded(final Object entity) {
		return emf.getPersistenceUnitUtil().isLoaded(entity);
	}

	public static Pageable createPageable(String... properties) {
		return PageRequest.of(0, 8, Sort.by(properties));
	}

	protected User 사용자_저장(final String email, final OAuthType oAuthType) {
		return userRepository.save(new User(new Email(email), oAuthType));
	}

	protected Profile 프로필_저장(final String username, final Category... categories) {
		return profileRepository.save(
			new Profile(username, new FavoriteCategories(Arrays.stream(categories).collect(toList()))));
	}

	protected Profile 사용자_프로필_동시_저장(
		final String email,
		final OAuthType oAuthType,
		final String username,
		final Category... categories
	) {
		final Profile profile = 프로필_저장(username, categories);
		사용자_저장(email, oAuthType).registerProfile(profile);
		return profile;
	}

	protected void 팔로우_저장(final Profile follower, final Profile followee) {
		follower.follow(followee);
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
		final long... tagIds
	) {
		return bookmarkRepository.save(new Bookmark(writer,
			linkMetadata.getId(),
			title,
			memo,
			openType,
			category,
			url,
			new TagIds(Arrays.stream(tagIds).boxed().collect(toList()))
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
		final long... tagIds
	) {
		return 북마크_저장(writer, 링크_메타데이터_저장(url, "제목 없음", "default-image.png"), title, "memo", openType, category, url,
			tagIds);
	}

	/* 주의 ! em.flush & em.clear occurs */
	protected Profile 좋아요_저장(final Profile profile, final Bookmark bookmark) {
		bookmark.requestReaction(profile.getId(), LIKE);
		bookmarkRepository.updateLikeCount(bookmark.getId(), null, LIKE);
		return 프로필_load(profile.getId());
	}

	/* 주의 ! em.flush & em.clear occurs */
	protected Profile 싫어요_저장(final Profile profile, final Bookmark bookmark) {
		bookmark.requestReaction(profile.getId(), HATE);
		bookmarkRepository.updateLikeCount(bookmark.getId(), null, HATE);
		return 프로필_load(profile.getId());
	}

	/* 주의 ! em.flush & em.clear occurs */
	protected Profile 리액션_요청(final Profile profile, final Bookmark bookmark, final ReactionType requestType) {
		if (requestType != null) {
			bookmark.requestReaction(profile.getId(), requestType);
			bookmarkRepository.updateLikeCount(bookmark.getId(), null, requestType);
			return 프로필_load(profile.getId());
		} else {
			return profile;
		}
	}

	protected void 즐겨찾기_저장(final Profile profile, final Bookmark bookmark) {
		profile.favorite(bookmark);
	}

	private Profile 프로필_load(final long profileId) {
		return profileRepository.findById(profileId).orElseThrow();
	}

}
