package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.FindLinkMetadataByUrlQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkServiceImpl implements BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final TagRepository tagRepository;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;

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

		/**
		 * bookmarkTag를 저장한다.
		 * 만약 존재하지 않는 tag가 있다면, 새로 만들어 저장하고 bookmarkTag를 만든다. (고민 - tag service를 만들 필요가 있을까 ?)
		 */
		command.getTagNames().stream()
			.map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName))))
			.forEach(savedBookmark::addBookmarkTag);

		return savedBookmark.getId();
	}

	//TODO
	@Override
	public GetBookmarkResult getBookmark(final long userId, final long bookmarkId) {
		return null;
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
