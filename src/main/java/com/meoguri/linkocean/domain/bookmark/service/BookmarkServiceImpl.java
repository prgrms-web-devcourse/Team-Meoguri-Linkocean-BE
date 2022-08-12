package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.*;
import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionQuery;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.FindLinkMetadataByUrlQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFavoriteQuery;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.service.TagService;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkServiceImpl implements BookmarkService {

	private final TagService tagService;

	private final BookmarkRepository bookmarkRepository;

	private final CheckIsFollowQuery checkIsFollowQuery;
	private final CheckIsFavoriteQuery checkIsFavoriteQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;
	private final ReactionQuery reactionQuery;

	/**
	 * 북마크 등록
	 * - 북마크 등록을 위해 항상 linkMetadata 가 먼저 저장 되어 있어야 한다.
	 */
	@Transactional
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {
		final long profileId = command.getProfileId();
		final String url = command.getUrl();

		/* 연관 필드 조회 */
		final Profile profile = findProfileByIdQuery.findById(profileId);
		final LinkMetadata linkMetadata = findLinkMetadataByUrlQuery.findByUrl(url);

		/* 비즈니스 로직 검증 - 사용자는 [url]당 하나의 북마크를 가질 수 있다 */
		final Optional<Bookmark> oBookmark = bookmarkRepository.findByProfileAndLinkMetadata(profile, linkMetadata);
		checkUniqueConstraint(oBookmark, "이미 해당 url 의 북마크를 가지고 있습니다");

		/* 태그 조회/저장 */
		final List<Tag> tags = tagService.getOrSaveList(command.getTagNames());

		/* 북마크 등록 진행 */
		return bookmarkRepository.save(new Bookmark(
			profile,
			linkMetadata,
			command.getTitle(),
			command.getMemo(),
			command.getOpenType(),
			command.getCategory(),
			command.getUrl(),
			tags
		)).getId();
	}

	@Transactional
	@Override
	public void updateBookmark(final UpdateBookmarkCommand command) {
		/* 수정 할 북마크 조회 */
		Bookmark bookmark = bookmarkRepository
			.findByProfileIdAndId(command.getProfileId(), command.getBookmarkId())
			.orElseThrow(LinkoceanRuntimeException::new);

		/* 태그 조회/저장 */
		final List<Tag> tags = tagService.getOrSaveList(command.getTagNames());

		/* update 진행 */
		bookmark.update(
			command.getTitle(),
			command.getMemo(),
			command.getCategory(),
			command.getOpenType(),
			tags
		);
	}

	@Transactional
	@Override
	public void removeBookmark(final long profileId, final long bookmarkId) {
		/* 제거 할 북마크 조회 */
		final Bookmark bookmark = bookmarkRepository
			.findByProfileIdAndId(profileId, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		/* remove 진행 */
		bookmark.remove();
	}

	@Override
	public GetDetailedBookmarkResult getDetailedBookmark(final long profileId, final long bookmarkId) {
		/* 북마크 조회 */
		final Bookmark bookmark = bookmarkRepository
			.findByIdFetchProfileAndLinkMetadataAndTags(bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		final Profile writer = bookmark.getProfile();

		/* 추가 정보 조회 */
		final boolean isFavorite = checkIsFavoriteQuery.isFavorite(profileId, bookmark);
		final boolean isFollow = checkIsFollowQuery.isFollow(profileId, writer);

		final Map<ReactionType, Long> reactionCountMap = reactionQuery.getReactionCountMap(bookmark);
		final Map<ReactionType, Boolean> reactionMap = reactionQuery.getReactionMap(profileId, bookmark);

		/* 결과 반환 */
		return new GetDetailedBookmarkResult(
			bookmarkId,
			bookmark.getTitle(),
			bookmark.getUrl(),
			bookmark.getLinkMetadata().getImage(),
			bookmark.getCategory(),
			bookmark.getMemo(),
			bookmark.getOpenType(),
			bookmark.getUpdatedAt(),
			isFavorite,
			bookmark.getTagNames(),
			reactionCountMap,
			reactionMap,
			new ProfileResult(
				writer.getId(),
				writer.getUsername(),
				writer.getImage(),
				isFollow
			)
		);
	}

	@Override
	public Page<GetBookmarksResult> getByTargetProfileId(
		final BookmarkFindCond findCond,
		final Pageable pageable
	) {
		final long currentUserProfileId = findCond.getCurrentUserProfileId();
		final Long targetProfileId = findCond.getTargetProfileId();

		/* 이용 가능한 open type 설정 */
		findCond.setOpenType(getAvailableBookmarkOpenType(currentUserProfileId, targetProfileId));

		/* 북마크 조회 */
		final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTargetProfileId(findCond, pageable);
		final List<Bookmark> bookmarks = bookmarkPage.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(currentUserProfileId, bookmarks);
		final boolean isWriter = currentUserProfileId == findCond.getTargetProfileId();

		/* 결과 반환 */
		return toResultPage(bookmarkPage, isFavorites, isWriter, pageable);
	}

	@Override
	public Page<GetFeedBookmarksResult> getFeedBookmarks(
		final BookmarkFindCond findCond,
		final Pageable pageable
	) {
		final long currentUserProfileId = findCond.getCurrentUserProfileId();

		/* 북마크 조회 */
		final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);
		final List<Bookmark> bookmarks = bookmarkPage.getContent();
		final List<Profile> writers = bookmarks.stream().map(Bookmark::getProfile).collect(toList());

		/* 추가 정보 조회 */
		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(currentUserProfileId, bookmarks);
		final List<Boolean> isFollows = checkIsFollowQuery.isFollows(currentUserProfileId, writers);

		return toResultPage(bookmarkPage, isFavorites, isFollows, currentUserProfileId, pageable);
	}

	@Override
	public Optional<Long> getBookmarkIdIfExist(final long profileId, final String url) {
		return bookmarkRepository.findBookmarkIdByProfileIdAndUrl(profileId, url);
	}

	/**
	 * 공개 범위 조건 - 북마크 작성자와 자신의 관계에 따라 결정 된다
	 * @see BookmarkFindCond
	 */
	private OpenType getAvailableBookmarkOpenType(final long currentUserProfileId, final long writerProfileId) {
		if (currentUserProfileId == writerProfileId) {
			return OpenType.PRIVATE;
		} else if (checkIsFollowQuery.isFollow(currentUserProfileId, findProfileByIdQuery.findById(writerProfileId))) {
			return OpenType.PARTIAL;
		} else {
			return OpenType.ALL;
		}
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
		final List<Boolean> isFavorites,
		final boolean isWriter,
		final Pageable pageable
	) {
		final List<GetBookmarksResult> bookmarkResults = new ArrayList<>();
		final List<Bookmark> bookmarks = bookmarkPage.getContent();

		int size = bookmarks.size();
		for (int i = 0; i < size; ++i) {
			final Bookmark bookmark = bookmarks.get(i);
			bookmarkResults.add(new GetBookmarksResult(
				bookmark.getId(),
				bookmark.getUrl(),
				bookmark.getTitle(),
				bookmark.getOpenType(),
				bookmark.getCategory(),
				bookmark.getUpdatedAt(),
				isFavorites.get(i),
				bookmark.getLikeCount(),
				bookmark.getLinkMetadata().getImage(),
				isWriter,
				bookmark.getTagNames()
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
		final List<Boolean> isFavorites,
		final List<Boolean> isFollows,
		final long currentUserProfileId,
		final Pageable pageable
	) {
		final List<GetFeedBookmarksResult> bookmarkResults = new ArrayList<>();
		final List<Bookmark> bookmarks = bookmarkPage.getContent();

		int size = bookmarks.size();
		for (int i = 0; i < size; ++i) {
			final Bookmark bookmark = bookmarks.get(i);
			final Profile writer = bookmark.getProfile();
			bookmarkResults.add(new GetFeedBookmarksResult(
				bookmark.getId(),
				bookmark.getTitle(),
				bookmark.getUrl(),
				bookmark.getOpenType(),
				bookmark.getCategory(),
				bookmark.getUpdatedAt(),
				bookmark.getLinkMetadata().getImage(),
				bookmark.getLikeCount(),
				isFavorites.get(i),
				writer.getId().equals(currentUserProfileId),
				bookmark.getTagNames(),
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

	@Override
	public int updateBookmarkLikeCount(long profileId, Long bookmarkLikeCount) {
		return bookmarkRepository.addBookmarkLikeCount(profileId, bookmarkLikeCount);
	}
}
