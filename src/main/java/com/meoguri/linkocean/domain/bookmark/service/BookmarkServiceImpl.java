package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static com.meoguri.linkocean.exception.Preconditions.*;
import static com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService.*;
import static java.lang.String.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindUsedTagIdWithCountResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetUsedTagWithCountResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.FindLinkMetadataByIdQuery;
import com.meoguri.linkocean.domain.linkmetadata.persistence.FindLinkMetadataByUrlQuery;
import com.meoguri.linkocean.domain.notification.service.NotificationService;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.service.ProfileQueryService;
import com.meoguri.linkocean.domain.tag.service.TagService;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkServiceImpl implements BookmarkService {

	private final TagService tagService;
	private final NotificationService notificationService;

	private final ProfileQueryService profileQueryService;

	private final BookmarkRepository bookmarkRepository;

	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;
	private final FindLinkMetadataByIdQuery findLinkMetadataByIdQuery;

	/* 북마크 등록 */
	@Transactional
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {
		final long writerId = command.getWriterId();
		final String url = command.getUrl();

		/* 연관 필드 조회 */
		final Profile writer = profileQueryService.findById(writerId);
		final Long linkMetadataId = findLinkMetadataByUrlQuery.findByUrl(url)
			.map(BaseIdEntity::getId).orElse(null);

		/* 비즈니스 로직 검증 - 사용자는 [url]당 하나의 북마크를 가질 수 있다 */
		final boolean exists = bookmarkRepository.existsByWriterAndUrl(writer, url);
		checkUniqueConstraint(exists, "이미 해당 url 의 북마크를 가지고 있습니다");

		/* 태그 조회/저장 */
		final TagIds tagIds = new TagIds(tagService.getOrSaveTags(command.getTagNames()));

		/* 북마크 등록 진행 */
		return bookmarkRepository.save(new Bookmark(
			writer,
			linkMetadataId,
			command.getTitle(),
			command.getMemo(),
			command.getOpenType(),
			command.getCategory(),
			command.getUrl(),
			tagIds
		)).getId();
	}

	@Transactional
	@Override
	public void updateBookmark(final UpdateBookmarkCommand command) {
		final long bookmarkId = command.getBookmarkId();

		/* 수정 할 북마크 조회 */
		Bookmark bookmark = bookmarkRepository.findByIdAndWriterId(bookmarkId, command.getWriterId())
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such bookmark id :%d", bookmarkId)));

		/* 태그 조회/저장 */
		final TagIds tagIds = new TagIds(tagService.getOrSaveTags(command.getTagNames()));

		/* update 진행 */
		bookmark.update(
			command.getTitle(),
			command.getMemo(),
			command.getCategory(),
			command.getOpenType(),
			tagIds
		);
	}

	@Transactional
	@Override
	public void removeBookmark(final long writerId, final long bookmarkId) {
		/* 제거 할 북마크 조회 */
		final Bookmark bookmark = bookmarkRepository
			.findByIdAndWriterId(bookmarkId, writerId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such bookmark id :%d", bookmarkId)));

		/* remove 진행 */
		bookmark.remove();
	}

	@Override
	public GetDetailedBookmarkResult getDetailedBookmark(final long profileId, final long bookmarkId) {
		/* 대상 북마크 조회 */
		final Bookmark bookmark = bookmarkRepository
			.findByIdFetchAll(bookmarkId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such bookmark id :%d", bookmarkId)));

		/* 추가 정보 조회 */
		final Profile writer = bookmark.getWriter();
		final boolean follow = profileQueryService.findProfileFetchFollows(profileId).isFollow(writer);
		final boolean favorite = profileQueryService.findProfileFetchFavoriteById(profileId).isFavorite(bookmark);

		final String linkMetaDataImage = bookmark.getLinkMetadataId()
			.map(linkMetadataId -> findLinkMetadataByIdQuery.findById(linkMetadataId).getImage())
			.orElse(DEFAULT_IMAGE);

		final Map<ReactionType, Long> reactionCountMap = bookmark.countReactionGroup();
		final Map<ReactionType, Boolean> reactionMap = bookmark.checkReaction(profileId);
		final Set<String> tags = tagService.getTags(bookmark.getTagIds());

		/* 결과 반환 */
		return new GetDetailedBookmarkResult(
			bookmarkId,
			bookmark.getTitle(),
			bookmark.getUrl(),
			linkMetaDataImage,
			bookmark.getCategory(),
			bookmark.getMemo(),
			bookmark.getOpenType(),
			bookmark.getCreatedAt(),
			favorite,
			tags,
			reactionCountMap,
			reactionMap,
			new ProfileResult(
				writer.getId(),
				writer.getUsername(),
				writer.getImage(),
				follow
			)
		);
	}

	@Override
	public Page<GetBookmarksResult> getByTargetProfileId(
		final BookmarkFindCond findCond,
		final Pageable pageable
	) {
		final long profileId = findCond.getCurrentUserProfileId();
		final Profile profile = profileQueryService.findProfileFetchFollows(profileId);
		final Profile target = profileQueryService.findById(findCond.getTargetProfileId());

		/* 이용 가능한 open type 설정 */
		findCond.setOpenType(profile.getAvailableBookmarkOpenType(target));

		/* 북마크 조회 */
		final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);
		final List<Bookmark> bookmarks = bookmarkPage.getContent();

		/* 추가 정보 조회 */
		final Set<LinkMetadata> linkMetadataSet = findLinkMetadataByIdQuery.findByIds(
			bookmarks.stream()
				.map(Bookmark::getLinkMetadataId)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(toList()));

		final Profile currentUserProfile = profileQueryService.findProfileFetchFavoriteById(profileId);
		final List<Boolean> isFavorites = currentUserProfile.isFavoriteBookmarks(bookmarks);

		/* 결과 반환 */
		return toResultPage(bookmarkPage, linkMetadataSet, isFavorites, profileId, pageable);
	}

	@Override
	public Page<GetFeedBookmarksResult> getFeedBookmarks(
		final BookmarkFindCond findCond,
		final Pageable pageable
	) {
		final long profileId = findCond.getCurrentUserProfileId();
		final Profile profile = profileQueryService.findProfileFetchFavoriteById(profileId);

		/* 북마크 조회 */
		final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);
		final List<Bookmark> bookmarks = bookmarkPage.getContent();
		final List<Profile> writers = bookmarks.stream().map(Bookmark::getWriter).collect(toList());

		/* 추가 정보 조회 */
		final Set<LinkMetadata> linkMetadataSet = findLinkMetadataByIdQuery.findByIds(
			bookmarks.stream().map(BaseIdEntity::getId).collect(toList()));
		final List<Boolean> isFavorites = profile.isFavoriteBookmarks(bookmarks);
		final List<Boolean> isFollows = profile.isFollows(writers);

		return toResultPage(bookmarkPage, linkMetadataSet, isFavorites, isFollows, profileId, pageable);
	}

	/* 북마크 공유 알림 */
	@Transactional
	@Override
	public void shareNotification(final long profileId, final long targetId, final long bookmarkId) {
		final ShareNotificationCommand command = new ShareNotificationCommand(profileId, targetId, bookmarkId);
		notificationService.shareNotification(command);
	}

	@Override
	public Optional<Long> getBookmarkIdIfExist(final long profileId, final String url) {
		return bookmarkRepository.findIdByWriterIdAndUrl(profileId, url);
	}

	/**
	 * 북마크 페이지를 즐겨찾기 여부를 포함한 북마크 조회 결과 페이지로 전환 한다
	 * @param bookmarkPage   북마크 페이지
	 * @param isFavorites    북마크별 즐겨찾기 여부
	 * @param pageable       페이지 정보
	 * @return 북마크 조회 결과 페이지
	 */
	private Page<GetBookmarksResult> toResultPage(
		final Page<Bookmark> bookmarkPage,
		final Set<LinkMetadata> linkMetadataSet,
		final List<Boolean> isFavorites,
		final long currentUserProfileId,
		final Pageable pageable
	) {
		final List<GetBookmarksResult> bookmarkResults = new ArrayList<>();
		final List<Bookmark> bookmarks = bookmarkPage.getContent();
		final List<Set<String>> tagsList =
			tagService.getTagsList(bookmarks.stream().map(Bookmark::getTagIds).collect(toList()));

		int size = bookmarks.size();
		for (int i = 0; i < size; ++i) {
			final Bookmark bookmark = bookmarks.get(i);
			final Profile writer = bookmark.getWriter();
			bookmarkResults.add(new GetBookmarksResult(
				bookmark.getId(),
				bookmark.getUrl(),
				bookmark.getTitle(),
				bookmark.getOpenType(),
				bookmark.getCategory(),
				bookmark.getCreatedAt(),
				isFavorites.get(i),
				bookmark.getLikeCount(),
				getLinkMetadataImage(bookmark, linkMetadataSet),
				writer.getId().equals(currentUserProfileId),
				tagsList.get(i)
			));
		}
		final long totalCount = bookmarkPage.getTotalElements();

		return new PageImpl<>(bookmarkResults, pageable, totalCount);
	}

	/**
	 * 북마크 페이지를 즐겨찾기 여부를 포함한 북마크 조회 결과 페이지로 전환 한다
	 * @param bookmarkPage   북마크 페이지
	 * @param isFavorites    북마크별 즐겨찾기 여부
	 * @param pageable       페이지 정보
	 * @return 북마크 조회 결과 페이지
	 */
	private Page<GetFeedBookmarksResult> toResultPage(
		final Page<Bookmark> bookmarkPage,
		final Set<LinkMetadata> linkMetadataSet,
		final List<Boolean> isFavorites,
		final List<Boolean> isFollows,
		final long currentUserProfileId,
		final Pageable pageable
	) {
		final List<GetFeedBookmarksResult> bookmarkResults = new ArrayList<>();
		final List<Bookmark> bookmarks = bookmarkPage.getContent();
		final List<Set<String>> tagsList =
			tagService.getTagsList(bookmarks.stream().map(Bookmark::getTagIds).collect(toList()));

		int size = bookmarks.size();
		for (int i = 0; i < size; ++i) {
			final Bookmark bookmark = bookmarks.get(i);
			final Profile writer = bookmark.getWriter();
			bookmarkResults.add(new GetFeedBookmarksResult(
				bookmark.getId(),
				bookmark.getTitle(),
				bookmark.getUrl(),
				bookmark.getOpenType(),
				bookmark.getCategory(),
				bookmark.getCreatedAt(),
				getLinkMetadataImage(bookmark, linkMetadataSet),
				bookmark.getLikeCount(),
				isFavorites.get(i),
				writer.getId().equals(currentUserProfileId),
				tagsList.get(i),
				new GetFeedBookmarksResult.ProfileResult(
					writer.getId(),
					writer.getUsername(),
					writer.getImage(),
					isFollows.get(i)
				)
			));
		}
		final long totalCount = bookmarkPage.getTotalElements();

		return new PageImpl<>(bookmarkResults, pageable, totalCount);
	}

	/**
	 * 북마크 링크 메타데이터 이미지를 반환한다.
	 * 만약 링크 메타데이터가 존재하지 않으면 DEFAULT_IMAEG를 반환한다.
	 */
	private String getLinkMetadataImage(final Bookmark bookmark, final Set<LinkMetadata> linkMetadataSet) {
		return linkMetadataSet.stream()
			.filter(linkMetadata -> linkMetadata.getId().equals(bookmark.getLinkMetadataId().orElse(null)))
			.map(LinkMetadata::getImage)
			.findFirst().orElse(DEFAULT_IMAGE);
	}

	@Override
	public List<GetUsedTagWithCountResult> getUsedTagsWithCount(final long profileId) {
		final List<FindUsedTagIdWithCountResult> tagIdsWithCount = bookmarkRepository.findUsedTagIdsWithCount(
			profileId);

		final List<Long> tagIds = tagIdsWithCount.stream()
			.map(FindUsedTagIdWithCountResult::getTagId)
			.collect(toList());
		final List<String> tags = tagService.getTags(tagIds);
		final int size = tags.size();

		return IntStream.range(0, size)
			.boxed()
			.map(it -> new GetUsedTagWithCountResult(
				tags.get(it),
				tagIdsWithCount.get(it).getCount()
			)).collect(toList());
	}
}
