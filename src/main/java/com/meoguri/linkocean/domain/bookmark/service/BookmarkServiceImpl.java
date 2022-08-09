package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.*;
import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.Collections;
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
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionQuery;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.UltimateBookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
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

	@Transactional
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {

		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final LinkMetadata linkMetadata = findLinkMetadataByUrlQuery.findByUrl(command.getUrl());

		final List<Tag> tags = Optional.ofNullable(command.getTagNames())
			.map(tagService::getOrSaveList)
			.orElseGet(Collections::emptyList);

		/* 북마크 생성 & 저장 */
		final Bookmark bookmark = new Bookmark(
			profile, linkMetadata,
			command.getTitle(),
			command.getMemo(),
			command.getOpenType(),
			command.getCategory(),
			command.getUrl(),
			tags
		);

		return bookmarkRepository.save(bookmark).getId();
	}

	@Transactional
	@Override
	public void updateBookmark(final UpdateBookmarkCommand command) {
		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final long bookmarkId = command.getBookmarkId();

		//userId, bookmarkId 유효성 검사
		Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(profile, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		final List<Tag> tags = tagService.getOrSaveList(command.getTagNames());

		//update 진행
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
		final Profile profile = findProfileByIdQuery.findById(profileId);

		// 자신의 북마크 쓴 북마크를 가져옴
		final Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(profile, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		bookmark.remove();
	}

	@Override
	public GetDetailedBookmarkResult getDetailedBookmark(final long profileId, final long bookmarkId) {

		final Bookmark bookmark = bookmarkRepository
			.findByIdFetchProfileAndLinkMetadataAndTags(bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		final Profile owner = bookmark.getProfile();
		final Profile currentUserProfile = findProfileByIdQuery.findById(profileId);
		final Profile profile = findProfileByIdQuery.findById(profileId);

		final boolean isFavorite = checkIsFavoriteQuery.isFavorite(owner, bookmark);
		final boolean isFollow = checkIsFollowQuery.isFollow(currentUserProfile, owner);

		final Map<ReactionType, Long> reactionCountMap = reactionQuery.getReactionCountMap(bookmark);
		final Map<ReactionType, Boolean> reactionMap = reactionQuery.getReactionMap(profile, bookmark);

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
			convertToProfileResult(bookmark.getProfile(), isFollow)
		);
	}

	private GetBookmarkProfileResult convertToProfileResult(final Profile profile, boolean isFollow) {
		return new GetBookmarkProfileResult(
			profile.getId(), profile.getUsername(), profile.getImage(), isFollow
		);
	}

	@Override
	public List<GetFeedBookmarksResult> getFeedBookmarks(final FeedBookmarksSearchCond searchCond) {
		return null;
	}

	@Override
	public Page<GetBookmarksResult> ultimateGetBookmarks(
		final UltimateBookmarkFindCond findCond,
		final Pageable pageable
	) {
		final long currentUserProfileId = findCond.getCurrentUserProfileId();

		final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

		final List<Boolean> isFavorites =
			checkIsFavoriteQuery.isFavorites(currentUserProfileId, bookmarkPage.getContent());
		final List<Boolean> isWriters = new ArrayList<>(nCopies(bookmarkPage.getSize(), true)); // 일단 항상 true 로 전달

		return toResultPage(bookmarkPage, isFavorites, isWriters, pageable);
	}

	/**
	 * 북마크 페이지를 즐겨찾기 여부를 포함한 북마크 조회 결과 페이지로 전환 한다
	 * @param bookmarkPage   북마크 페이지를
	 * @param isFavorites    북마크별 즐겨찾기 여부
	 * @param pageable       페이지 정보
	 * @return 북마크 조회 결과 페이지
	 */
	private Page<GetBookmarksResult> toResultPage(
		final Page<Bookmark> bookmarkPage,
		final List<Boolean> isFavorites,
		final List<Boolean> isWriter,
		final Pageable pageable
	) {
		final List<GetBookmarksResult> bookmarkResults = new ArrayList<>();
		final List<Bookmark> bookmarks = bookmarkPage.getContent();

		int size = bookmarks.size();
		for (int i = 0; i < size; ++i) {
			bookmarkResults.add(new GetBookmarksResult(
				bookmarks.get(i).getId(),
				bookmarks.get(i).getUrl(),
				bookmarks.get(i).getTitle(),
				bookmarks.get(i).getOpenType(),
				bookmarks.get(i).getCategory(),
				bookmarks.get(i).getUpdatedAt(),
				isFavorites.get(i),
				bookmarks.get(i).getLikeCount(),
				bookmarks.get(i).getLinkMetadata().getImage(),
				isWriter.get(i),
				bookmarks.get(i).getTagNames()
			));
		}
		final long totalCount = bookmarkPage.getTotalElements();

		return new PageImpl<>(bookmarkResults, pageable, totalCount);
	}

	@Override
	public Optional<Long> getBookmarkToCheck(final long profileId, final String url) {
		return bookmarkRepository.findBookmarkIdByProfileIdAndUrl(profileId, url);
	}
}
