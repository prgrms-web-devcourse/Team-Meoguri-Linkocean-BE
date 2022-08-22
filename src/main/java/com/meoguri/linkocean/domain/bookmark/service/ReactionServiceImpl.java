package com.meoguri.linkocean.domain.bookmark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final BookmarkService bookmarkService;

	private final ReactionRepository reactionRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Transactional
	@Override
	public void requestReaction(ReactionCommand command) {
		final long profileId = command.getProfileId();
		final long bookmarkId = command.getBookmarkId();

		final Profile profile = findProfileByIdQuery.findById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);
		final ReactionType requestType = command.getReactionType();

		final Optional<Reaction> oReaction = reactionRepository.findByProfile_idAndBookmark(profileId, bookmark);
		final boolean isAlreadyReacted = oReaction.isPresent();
		final ReactionType existedType = isAlreadyReacted ? oReaction.get().getType() : null;

		if (isAlreadyReacted) {
			if (existedType.equals(requestType)) {
				/* 북마크에 리액션을 가지고 있으며 같은 타입의 리액션을 하는 경우 취소 */
				reactionRepository.deleteByProfile_idAndBookmark_id(profileId, bookmarkId);
			} else {
				/* 북마크에 리액션을 가지고 있으며 다른 타입의 리액션을 하는 경우 변경*/
				reactionRepository.updateReaction(profileId, bookmarkId, requestType);
			}
		} else {
			/* 북마크에 리액션을 가지고 있지 않으면 추가 */
			reactionRepository.save(new Reaction(profile, bookmark, requestType));
		}

		/* 북마크 좋아요 숫자 업데이트 */
		bookmarkService.updateLikeCount(bookmarkId, isAlreadyReacted, existedType, requestType);
	}

}
