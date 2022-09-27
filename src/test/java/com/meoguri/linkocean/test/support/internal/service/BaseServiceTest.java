package com.meoguri.linkocean.test.support.internal.service;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.internal.bookmark.entity.vo.ReactionType.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.internal.bookmark.service.BookmarkService;
import com.meoguri.linkocean.internal.bookmark.service.ReactionService;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.internal.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.internal.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.internal.linkmetadata.service.LinkMetadataService;
import com.meoguri.linkocean.internal.notification.service.NotificationService;
import com.meoguri.linkocean.internal.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.internal.profile.command.service.FavoriteService;
import com.meoguri.linkocean.internal.profile.command.service.FollowService;
import com.meoguri.linkocean.internal.profile.command.service.ProfileService;
import com.meoguri.linkocean.internal.profile.command.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.internal.profile.query.service.ProfileQueryService;
import com.meoguri.linkocean.internal.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.internal.user.domain.RefreshTokenRepository;
import com.meoguri.linkocean.internal.user.domain.UserService;
import com.meoguri.linkocean.internal.user.domain.dto.GetUserResult;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;
import com.meoguri.linkocean.test.support.db.DatabaseCleanup;

@ServiceTest
public abstract class BaseServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProfileQueryService profileQueryService;

	@Autowired
	private FollowService followService;

	@Autowired
	private FavoriteService favoriteService;

	@Autowired
	private ReactionService reactionService;

	@Autowired
	private LinkMetadataService linkMetadataService;

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	protected DatabaseCleanup databaseCleanup;

	@AfterEach
	void cleanUp() {
		databaseCleanup.execute();
		refreshTokenRepository.deleteAll();
	}

	public static Pageable createPageable(String... properties) {
		return PageRequest.of(0, 8, Sort.by(properties));
	}

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

	protected void 즐겨찾기(final long profileId, final long bookmarkId) {
		favoriteService.favorite(profileId, bookmarkId);
	}

	protected long 북마크_등록(final long writerId, final String url, final OpenType openType) {
		return bookmarkService.registerBookmark(
			new RegisterBookmarkCommand(writerId, url, "title", "memo", Category.IT, openType, emptyList())
		);
	}

	protected void 북마크_삭제(final long writerId, final long bookmarkId) {
		bookmarkService.removeBookmark(writerId, bookmarkId);
	}

	protected long 북마크_링크_메타데이터_동시_등록(final long writerId, final String url, final OpenType openType) {
		return 북마크_링크_메타데이터_동시_등록(writerId, url, null, null, null, openType);
	}

	protected long 북마크_링크_메타데이터_동시_등록(final long writerId, final String url, final String... tags) {
		return 북마크_링크_메타데이터_동시_등록(writerId, url, null, tags);
	}

	protected long 북마크_링크_메타데이터_동시_등록(final long writerId, final String url, final Category category,
		final String... tags) {
		final String title = linkMetadataService.obtainTitle(url);

		return 북마크_링크_메타데이터_동시_등록(writerId, url, title, null, category, ALL, tags);
	}

	protected long 북마크_링크_메타데이터_동시_등록(
		final long writerId,
		final String url,
		final String title,
		final String memo,
		final Category category,
		final OpenType openType,
		final String... tags
	) {
		linkMetadataService.obtainTitle(url);
		final List<String> tagList = Arrays.stream(tags).collect(toList());

		return bookmarkService.registerBookmark(
			new RegisterBookmarkCommand(writerId, url, title, memo, category, openType, tagList)
		);
	}

	protected String 링크_제목_얻기(final String url) {
		return linkMetadataService.obtainTitle(url);
	}

	protected GetDetailedBookmarkResult 북마크_상세_조회(final long profileId, final long bookmarkId) {
		return bookmarkService.getDetailedBookmark(profileId, bookmarkId);
	}

	protected void 좋아요_요청(final long profileId, final long bookmarkId) {
		reactionService.requestReaction(new ReactionCommand(profileId, bookmarkId, LIKE));
	}

	protected void 싫어요_요청(final long profileId, final long bookmarkId) {
		reactionService.requestReaction(new ReactionCommand(profileId, bookmarkId, HATE));
	}

	protected void 북마크_공유(final long senderId, final long receiverId, final long bookmarkId) {
		notificationService.shareNotification(new ShareNotificationCommand(senderId, receiverId, bookmarkId));
	}

	protected GetDetailedProfileResult 내_프로필_상세_조회(final long currentUserProfileId) {
		return profileQueryService.getByProfileId(currentUserProfileId, currentUserProfileId);
	}

}
