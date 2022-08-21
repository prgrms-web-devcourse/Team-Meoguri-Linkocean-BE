package com.meoguri.linkocean.test.support.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.service.BookmarkService;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.linkmetadata.service.LinkMetadataService;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.service.FollowService;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;
import com.meoguri.linkocean.domain.user.service.UserService;
import com.meoguri.linkocean.domain.user.service.dto.GetUserResult;

//TODO - migrate to service logic
@Transactional
@SpringBootTest
public class BaseServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private FollowService followService;

	@Autowired
	private LinkMetadataService linkMetadataService;

	@Autowired
	private BookmarkService bookmarkService;

	protected long 사용자_없으면_등록(final String email, final OAuthType oAuthType) {
		return userService.registerIfNotExists(new Email(email), oAuthType);
	}

	protected GetUserResult 사용자_조회(final String email, final OAuthType oAuthType) {
		return userService.getUser(new Email(email), oAuthType);
	}

	protected long 프로필_등록(final long userId, final String username, final Category... categories) {
		return profileService.registerProfile(new RegisterProfileCommand(
			userId, username, Arrays.stream(categories).collect(toList()))
		);
	}

	protected long 사용자_프로필_동시_등록(
		final String email,
		final OAuthType oAuthType,
		final String username,
		final Category... categories
	) {
		return 프로필_등록(사용자_없으면_등록(email, oAuthType), username, categories);
	}

	protected void 팔로우(final long followerId, final long followeeId) {
		followService.follow(followerId, followeeId);
	}

	protected String 링크_제목_얻기(final String url) {
		return linkMetadataService.obtainTitle(url);
	}

	protected GetDetailedBookmarkResult 북마크_상세_조회(final long profileId, final long bookmarkId) {
		return bookmarkService.getDetailedBookmark(profileId, bookmarkId);
	}

	/* 아래의 코드 들은 BasePersistenceTest 의 복붙이며 제거 대상임
	   천천히 Service Logic 으로 migration 할 것 */
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

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

	protected void 즐겨찾기_저장(final Profile profile, final Bookmark bookmark) {
		profile.favorite(bookmark);
	}

}
