package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.*;
import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

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
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
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
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkServiceImpl implements BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final TagRepository tagRepository;

	private final CheckIsFollowQuery checkIsFollowQuery;
	private final CheckIsFavoriteQuery checkIsFavoriteQuery;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;
	private final ReactionQuery reactionQuery;

	//TODO : 쿼리 튜닝
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {

		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final LinkMetadata linkMetadata = findLinkMetadataByUrlQuery.findByUrl(command.getUrl());

		final List<Tag> tags = Optional.ofNullable(command.getTagNames())
			.map(wrapper -> convertTagNamesToTags(wrapper))
			.orElseGet(() -> Collections.emptyList());

		/* 북마크 생성 & 저장 */
		final Bookmark newBookmark = Bookmark.builder()
			.profile(profile)
			.linkMetadata(linkMetadata)
			.title(command.getTitle())
			.memo(command.getMemo())
			.openType(command.getOpenType())
			.category(command.getCategory())
			.url(command.getUrl())
			.tags(tags)
			.build();

		return bookmarkRepository.save(newBookmark).getId();
	}

	@Override
	public void updateBookmark(final UpdateBookmarkCommand command) {
		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final long bookmarkId = command.getBookmarkId();

		//userId, bookmarkId 유효성 검사
		Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(profile, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		//update 진행
		bookmark.update(
			command.getTitle(),
			command.getMemo(),
			command.getCategory(),
			command.getOpenType(),
			convertTagNamesToTags(command.getTagNames())
		);
	}

	@Override
	public void removeBookmark(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdQuery.findById(profileId);

		// 자신의 북마크 쓴 북마크를 가져옴
		final Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(profile, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		bookmark.remove();
	}

	//TODO : 쿼리 튜닝
	@Transactional(readOnly = true)
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

		return GetDetailedBookmarkResult.builder()
			.title(bookmark.getTitle())
			.url(bookmark.getUrl())
			.image(bookmark.getLinkMetadata().getImage())
			.category(bookmark.getCategory())
			.memo(bookmark.getMemo())
			.openType(bookmark.getOpenType())
			.isFavorite(isFavorite)
			.updatedAt(bookmark.getUpdatedAt())
			.tags(bookmark.getTagNames())
			.reactionCount(reactionCountMap)
			.reaction(reactionMap)
			.profile(convertToProfileResult(bookmark.getProfile(), isFollow))
			.build();
	}

	/**
	 * TagName 정보를 이용해 Tag를 만든다.
	 * 1. tag 이름이 존재하면 만들지 않고 db에서 가져온다.
	 * 2. tag 이름이 존재하지 않다면 태그를 만들고 db에 저장한다.
	 */
	private List<Tag> convertTagNamesToTags(final List<String> tagNames) {
		return tagNames.stream()
			.map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName))))
			.collect(toList());
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

	@Transactional(readOnly = true)
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
	public Optional<Long> getBookmarkToCheck(final long userId, final String url) {
		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);
		final Optional<Bookmark> oBookmark = bookmarkRepository.findByProfileAndUrl(profile, url);

		if (oBookmark.isPresent()) {
			return Optional.of(profile.getId());
		}
		return Optional.empty();
	}
}
