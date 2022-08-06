package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.*;
import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.OtherBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.PageResult;
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
	private final ReactionRepository reactionRepository;

	private final CheckIsFollowQuery checkIsFollowQuery;
	private final CheckIsFavoriteQuery checkIsFavoriteQuery;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;

	//TODO : 쿼리 튜닝
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {

		final Profile profile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final LinkMetadata linkMetadata = findLinkMetadataByUrlQuery.findByUrl(command.getUrl());

		/* 북마크 생성 & 저장 */
		final Bookmark newBookmark = Bookmark.builder()
			.profile(profile)
			.linkMetadata(linkMetadata)
			.title(command.getTitle())
			.memo(command.getMemo())
			.openType(command.getOpenType())
			.category(command.getCategory())
			.url(command.getUrl())
			.build();

		final Bookmark savedBookmark = bookmarkRepository.save(newBookmark);

		Optional.ofNullable(command.getTagNames())
			.ifPresent(tagNames -> convertTagNamesToTags(tagNames)
				.forEach(savedBookmark::addBookmarkTag));

		return savedBookmark.getId();
	}

	@Override
	public void updateBookmark(final UpdateBookmarkCommand command) {

		//userId, bookmarkId 유효성 검사
		Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(findProfileByUserIdQuery.findByUserId(command.getUserId()), command.getBookmarkId())
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
	public void removeBookmark(final long userId, final long bookmarkId) {
		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);

		// 자신의 북마크 쓴 북마크를 가져옴
		final Bookmark bookmark = bookmarkRepository
			.findByProfileAndId(profile, bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		bookmarkRepository.delete(bookmark);
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

	//TODO : 쿼리 튜닝
	@Transactional(readOnly = true)
	@Override
	public GetDetailedBookmarkResult getDetailedBookmark(final long userId, final long bookmarkId) {

		final Bookmark bookmark = bookmarkRepository
			.findByIdFetchProfileAndLinkMetadataAndTags(bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		final Profile owner = bookmark.getProfile();
		final Profile currentUserProfile = findProfileByUserIdQuery.findByUserId(userId);
		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);

		final boolean isFavorite = checkIsFavoriteQuery.isFavorite(owner, bookmark);
		final boolean isFollow = checkIsFollowQuery.isFollow(currentUserProfile, owner);

		return GetDetailedBookmarkResult.builder()
			.title(bookmark.getTitle())
			.url(bookmark.getLinkMetadata().getLink().getFullLink())
			.image(bookmark.getLinkMetadata().getImage())
			.category(bookmark.getCategory())
			.memo(bookmark.getMemo())
			.openType(bookmark.getOpenType())
			.isFavorite(isFavorite)
			.updatedAt(bookmark.getUpdatedAt())
			.tags(bookmark.getTagNames())
			.reactionCount(getReactionCountMap(bookmark))
			.reaction(getReactionMap(profile, bookmark))
			.profile(convertToProfileResult(bookmark.getProfile(), isFollow))
			.build();
	}

	//TODO 리액션 요청에서 북마크 좋아요 개수도 같이 수정하는 로직이 추가되면 이 부분 수정하기.
	private Map<String, Long> getReactionCountMap(Bookmark bookmark) {
		return Arrays.stream(ReactionType.values())
			.collect(toMap(ReactionType::getName, reactionType ->
				reactionRepository.countReactionByBookmarkAndType(bookmark, reactionType))
			);
	}

	private Map<String, Boolean> getReactionMap(final Profile profile, final Bookmark bookmark) {
		final Optional<Reaction> oMyReaction = reactionRepository.findByProfileAndBookmark(profile, bookmark);

		return Arrays.stream(ReactionType.values())
			.collect(toMap(ReactionType::getName, reactionType ->
				oMyReaction.map(reaction -> ReactionType.of(reaction.getType()).equals(reactionType)).orElse(false)
			));
	}

	private GetBookmarkProfileResult convertToProfileResult(final Profile profile, boolean isFollow) {
		return new GetBookmarkProfileResult(
			profile.getId(), profile.getUsername(), profile.getImage(), isFollow
		);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<GetBookmarksResult> getMyBookmarks(final MyBookmarkSearchCond searchCond, final Pageable pageable) {
		final String category = searchCond.getCategory();
		final boolean favorite = searchCond.isFavorite();
		final List<String> tags = searchCond.getTags();

		final Profile profile = findProfileByUserIdQuery.findByUserId(searchCond.getUserId());

		// Case 1 - 카테고리 필터링
		if (nonNull(category)) {
			// 카테고리 필터링이 들어오면 즐겨찾기와 태그 필터링은 없어야한다.
			checkCondition(!favorite && isNull(tags));

			return searchResultBy(
				Category.of(category),
				profile,
				searchCond.toFindBookmarksDefaultCond(profile.getId()),
				pageable
			);
		}

		/*// Case 2 - 즐겨찾기 필터링
		if (favorite) {
			// 즐겨찾기 필터링이 들어오면 카테고리와 태그 필터링은 없어야한다.
			checkCondition(isNull(searchCond.getTags()));
			return getPageResultBy(
				profile,
				favorite,
				searchCond.toFindBookmarksDefaultCond(profile.getId()),
				pageable
			);
		}*/

		/*// Case 3 - 태그 필터링
		if (nonNull(tags)) {
			// 태그는 최대 3개 까지 필터링 가능하다
			checkCondition(searchCond.getTags().size() <= 3);
			return getPageResultBy(
				profile,
				tags,
				searchCond.toFindBookmarksDefaultCond(profile.getId()),
				pageable
			);
		}*/

		// return getPageResultBy(profile, searchCond.toFindBookmarksDefaultCond(profile.getId()));
		throw new IllegalStateException("temp illegal state");
	}

	@Override
	public PageResult<GetBookmarksResult> getOtherBookmarks(final long userId,
		final OtherBookmarkSearchCond cond) {

		final Profile myProfile = findProfileByUserIdQuery.findByUserId(userId);
		final Profile otherProfile = findProfileByIdQuery.findById(cond.getOtherProfileId());

		List<OpenType> openTypes = new ArrayList<>();
		openTypes.add(OpenType.ALL);

		if (checkIsFollowQuery.isFollow(myProfile, otherProfile)) {
			openTypes.add(OpenType.PARTIAL);
		}

		final FindBookmarksDefaultCond defaultCond = cond.toFindBookmarksDefaultCond();
		defaultCond.changeOpenType(openTypes);

		if (nonNull(cond.getCategory())) {
			/* 카테고리 필터링이 들어오면 즐겨찾기와 태그 필터링은 없어야한다. */
			checkCondition(!cond.isFavorite() && isNull(cond.getTags()));
			return getBookmarksByCategoryAndDefaultCond(myProfile, Category.of(cond.getCategory()), defaultCond,
				PageRequest.of(1, 8));
		}

		if (cond.isFavorite()) {
			/* 즐겨찾기 필터링이 들어오면 카테고리와 태그 필터링은 없어야한다. */
			checkCondition(isNull(cond.getTags()));
			return getBookmarksByFavoriteAndDefaultCond(myProfile, cond.isFavorite(), defaultCond);
		}

		if (nonNull(cond.getTags())) {
			/* 태그는 최대 3개 까지 필터링 가능하다 */
			checkCondition(cond.getTags().size() <= 3);
			return getBookmarksByTagsAndDefaultCond(myProfile, cond.getTags(), defaultCond);
		}

		return getBookmarksByDefaultCond(myProfile, defaultCond);
	}

	private Page<GetBookmarksResult> searchResultBy(
		final Category category,
		final Profile profile,
		final FindBookmarksDefaultCond cond,
		final Pageable pageable
	) {
		final List<Bookmark> bookmarks = bookmarkRepository.searchByCategoryAndDefaultCond(category, cond, pageable);
		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(profile, bookmarks);
		final long totalCount = bookmarkRepository.countByCategoryAndDefaultCond(category, cond);

		return convert(bookmarks, isFavorites, pageable, totalCount);
	}

	/*private Page<GetBookmarksResult> getPageResultBy(
		final Profile myProfile,
		final boolean favorite,
		final FindBookmarksDefaultCond cond,
		final Pageable pageable
	) {
		//전체 개수 조회
		long totalCount = bookmarkRepository.countByCategoryAndDefaultCond(category, cond);

		//페이지에 맞게 조회
		final List<Bookmark> bookmarks = bookmarkRepository.searchByCategoryAndDefaultCond(category, cond);

		//즐겨 찾기 여부 리스트 한번에 가져오기.
		final List<Boolean> isFavorites = checkIsFavorite(myProfile, bookmarks);

		return convert(bookmarks, myProfile, isFavorites, pageable, totalCount);
	}*/

	/*private Page<GetBookmarksResult> getPageResultBy(
		final Profile myProfile,
		final List<String> tags,
		final FindBookmarksDefaultCond cond,
		final Pageable pageable
	) {
		//전체 개수 조회
		long totalCount = bookmarkRepository.countByCategoryAndDefaultCond(category, cond);

		//페이지에 맞게 조회
		final List<Bookmark> bookmarks = bookmarkRepository.searchByCategoryAndDefaultCond(category, cond);

		//즐겨 찾기 여부 리스트 한번에 가져오기.
		final List<Boolean> isFavorites = checkIsFavorite(myProfile, bookmarks);

		return convert(bookmarks, myProfile, isFavorites, pageable, totalCount);
	}*/

	private PageResult<GetBookmarksResult> getBookmarksByCategoryAndDefaultCond(
		final Profile myProfile,
		final Category category,
		final FindBookmarksDefaultCond cond,
		final Pageable pageable
	) {
		//전체 개수 조회
		long totalCount = bookmarkRepository.countByCategoryAndDefaultCond(category, cond);

		//페이지에 맞게 조회
		final List<Bookmark> bookmarks = bookmarkRepository.searchByCategoryAndDefaultCond(category, cond, pageable);

		//즐겨 찾기 여부 리스트 한번에 가져오기.
		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(myProfile, bookmarks);

		return convertToBookmarksResult(totalCount, bookmarks, myProfile, isFavorites);
	}

	private PageResult<GetBookmarksResult> getBookmarksByFavoriteAndDefaultCond(final Profile myProfile,
		final boolean favorite, final FindBookmarksDefaultCond cond) {
		long totalCount = bookmarkRepository.countByFavoriteAndDefaultCond(favorite, cond);

		final List<Bookmark> bookmarks = bookmarkRepository.searchByFavoriteAndDefaultCond(favorite, cond,
			PageRequest.of(1, 8));

		final List<Boolean> isFavorites;
		if (myProfile.getId().equals(cond.getProfileId())) {
			isFavorites = bookmarks.stream().map(bookmark -> true).collect(toList());
		} else {
			isFavorites = checkIsFavoriteQuery.isFavorites(myProfile, bookmarks);
		}

		return convertToBookmarksResult(totalCount, bookmarks, myProfile, isFavorites);
	}

	private PageResult<GetBookmarksResult> getBookmarksByTagsAndDefaultCond(final Profile myProfile,
		final List<String> tags, final FindBookmarksDefaultCond cond) {
		long totalCount = bookmarkRepository.countByTagsAndDefaultCond(tags, cond);

		final List<Bookmark> bookmarks = bookmarkRepository.searchByTagsAndDefaultCond(tags, cond,
			PageRequest.of(1, 8));

		//즐겨 찾기 여부 리스트 한번에 가져오기.
		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(myProfile, bookmarks);

		return convertToBookmarksResult(totalCount, bookmarks, myProfile, isFavorites);
	}

	private PageResult<GetBookmarksResult> getBookmarksByDefaultCond(final Profile myProfile,
		final FindBookmarksDefaultCond cond) {
		final long totalCount = bookmarkRepository.countByDefaultCond(cond);

		final List<Bookmark> bookmarks = bookmarkRepository.searchByDefaultCond(cond,
			PageRequest.of(1, 8));

		final List<Boolean> isFavorites = checkIsFavoriteQuery.isFavorites(myProfile, bookmarks);

		return convertToBookmarksResult(totalCount, bookmarks, myProfile, isFavorites);
	}

	/**
	 * @param bookmarks   조회한 북마크
	 * @param isFavorites 북마크의 즐겨찾기 여부
	 * @param pageable      페이지 정보
	 * @param totalCount  북마크의 총 개수
	 * @return 결과 페이지
	 */
	private Page<GetBookmarksResult> convert(
		final List<Bookmark> bookmarks,
		final List<Boolean> isFavorites,
		final Pageable pageable,
		final long totalCount
	) {
		final List<GetBookmarksResult> bookmarkResults = new ArrayList<>();
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
				true,        // 자신의 북마크 조회이므로 항상 참
				bookmarks.get(i).getTagNames()
			));
		}

		return new PageImpl<>(bookmarkResults, pageable, totalCount);
	}

	private PageResult<GetBookmarksResult> convertToBookmarksResult(final long totalCount,
		final List<Bookmark> bookmarks,
		final Profile myProfile, final List<Boolean> isFavorites) {

		final ArrayList<GetBookmarksResult> bookmarkResults = new ArrayList<>();
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
				bookmarks.get(i).getProfile().equals(myProfile),
				bookmarks.get(i).getTagNames()
			));
		}

		return PageResult.of(totalCount, bookmarkResults);
	}

	//TODO
	@Override
	public List<GetFeedBookmarksResult> getFeedBookmarks(final FeedBookmarksSearchCond searchCond) {
		return null;
	}
}
