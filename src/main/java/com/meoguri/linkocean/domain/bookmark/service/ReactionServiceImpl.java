package com.meoguri.linkocean.domain.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.command.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final BookmarkService bookmarkService;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Transactional
	@Override
	public void requestReaction(ReactionCommand command) {
		final long profileId = command.getProfileId();
		final long bookmarkId = command.getBookmarkId();

		final Profile profile = findProfileByIdQuery.findProfileFetchReactionById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);
		final ReactionType requestType = command.getReactionType();

		/* 리액션 요청 */
		final ReactionType existedType = profile.requestReaction(bookmark, requestType);

		/* 북마크 좋아요 숫자 업데이트 */
		bookmarkService.updateLikeCount(bookmarkId, existedType, requestType);
	}

}
