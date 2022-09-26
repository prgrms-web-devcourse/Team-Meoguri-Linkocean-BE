package com.meoguri.linkocean.internal.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.internal.bookmark.entity.Bookmark;
import com.meoguri.linkocean.internal.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.internal.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.internal.bookmark.persistence.FindBookmarkByIdRepository;
import com.meoguri.linkocean.internal.bookmark.service.dto.ReactionCommand;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final BookmarkRepository bookmarkRepository;

	private final FindBookmarkByIdRepository findBookmarkByIdRepository;

	@Transactional
	@Override
	public void requestReaction(ReactionCommand command) {
		final long profileId = command.getProfileId();
		final long bookmarkId = command.getBookmarkId();
		final ReactionType requestType = command.getReactionType();

		/* 리액션 요청 */
		final Bookmark bookmark = findBookmarkByIdRepository.findByIdFetchReactions(bookmarkId);
		final ReactionType existedType = bookmark.requestReaction(profileId, requestType);

		/* 북마크 좋아요 숫자 업데이트 */
		bookmarkRepository.updateLikeCount(bookmarkId, existedType, requestType);
	}

}
