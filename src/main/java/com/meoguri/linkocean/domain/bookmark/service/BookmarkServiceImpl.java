package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.FavoriteRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.FindLinkMetadataByUrlQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkServiceImpl implements BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final TagRepository tagRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReactionRepository reactionRepository;
	private final FollowRepository followRepository;

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;

	//TODO : 쿼리 튜닝
	@Override
	public long registerBookmark(final RegisterBookmarkCommand command) {

		final Profile profile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final LinkMetadata linkMetadata = findLinkMetadataByUrlQuery.findByUrl(command.getUrl());

		/* 동일한 url의 북마크는 만들 수 없다. */
		bookmarkRepository.findByProfileAndLinkMetadata(profile, linkMetadata)
			.ifPresent(bookmark -> {
				throw new IllegalArgumentException(String.format("%s의 북마크가 이미 존재합니다.", command.getUrl()));
			});

		/* 북마크 생성 & 저장 */
		final Bookmark newBookmark = Bookmark.builder()
			.profile(profile)
			.linkMetadata(linkMetadata)
			.title(command.getTitle())
			.memo(command.getMemo())
			.category(command.getCategory())
			.openType(command.getOpenType())
			.build();

		final Bookmark savedBookmark = bookmarkRepository.save(newBookmark);

		convertTagNamesToTags(command.getTagNames()).forEach(savedBookmark::addBookmarkTag);

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

	/**
	 * TagName 정보를 이용해 Tag를 만든다.
	 * 1. tag 이름이 존재하면 만들지 않고 db에서 가져온다.
	 * 2. tag 이름이 존재하지 않다면 태그를 만들고 db에 저장한다.
	 */
	private List<Tag> convertTagNamesToTags(final List<String> tagNames) {
		return tagNames.stream()
			.map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName))))
			.collect(Collectors.toList());
	}

	//TODO : 쿼리 튜닝
	@Transactional(readOnly = true)
	@Override
	public GetBookmarkResult getBookmark(final long userId, final long bookmarkId) {

		final Bookmark bookmark = bookmarkRepository.findByIdWithProfileAndLinkMetadataAndTags(bookmarkId)
			.orElseThrow(LinkoceanRuntimeException::new);

		boolean isFavorite = favoriteRepository.existsByOwnerAndBookmark(bookmark.getProfile(), bookmark);

		final long likeCnt = reactionRepository.countLikeByBookmark(bookmark);
		final long hateCnt = reactionRepository.countHateByBookmark(bookmark);

		final boolean isFollow = followRepository.findByProfiles(
			bookmark.getProfile(),
			findProfileByUserIdQuery.findByUserId(userId)
		).isPresent();

		return builder()
			.title(bookmark.getTitle())
			.url(bookmark.getLinkMetadata().getUrl().getUrlWithSchemaAndWww())
			.imageUrl(bookmark.getLinkMetadata().getImageUrl())
			.category(bookmark.getCategory())
			.memo(bookmark.getMemo())
			.openType(bookmark.getOpenType())
			.isFavorite(isFavorite)
			.updatedAt(bookmark.getUpdatedAt())
			.tags(bookmark.getTagNames())
			.reactionCount(convertToReactionMap(likeCnt, hateCnt))
			.profile(convertToProfileResult(bookmark.getProfile(), isFollow))
			.build();
	}

	private Map<String, Long> convertToReactionMap(final long likeCnt, final long hateCnt) {
		final Map<String, Long> map = new HashMap<>();
		map.put("like", likeCnt);
		map.put("hate", hateCnt);
		return map;
	}

	private GetBookmarkProfileResult convertToProfileResult(final Profile profile, boolean isFollow) {
		return new GetBookmarkProfileResult(
			profile.getId(), profile.getUsername(), profile.getImageUrl(), isFollow
		);
	}

	//TODO
	@Override
	public List<GetMyBookmarksResult> getMyBookmarks(final MyBookmarkSearchCond searchCond) {
		return null;
	}

	//TODO
	@Override
	public List<GetFeedBookmarksResult> getFeedBookmarks(final FeedBookmarksSearchCond searchCond) {
		return null;
	}
}
