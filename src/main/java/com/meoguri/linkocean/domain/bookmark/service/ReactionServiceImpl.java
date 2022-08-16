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
@Transactional
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final BookmarkService bookmarkService;

	private final ReactionRepository reactionRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Override
	public void requestReaction(ReactionCommand command) {
		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());
		final ReactionType requestReactionType = command.getReactionType();

		final Optional<Reaction> oReaction = reactionRepository.findByProfile_idAndBookmark(profile.getId(), bookmark);
		final long bookmarkId = bookmark.getId();

		/* 리액션이 존재하는 경우 */
		if (oReaction.isPresent()) {
			final Reaction reaction = oReaction.get();
			final ReactionType existedReactionType = reaction.getType();

			/*이미 있던 reaction의 reactionType 이 request reactionType 과 같다면*/
			if (existedReactionType.equals(requestReactionType)) {
				/*리액션 삭제(취소)*/
				cancelReaction(reaction);

				if (existedReactionType.equals(ReactionType.LIKE)) {
					bookmarkService.subtractLikeCount(bookmarkId);
				}

				/*이미 있던 reaction의 reactionType 이 request reactionType 과 다르다면*/
			} else {
				/*리액션 변경*/
				changeReaction(reaction, requestReactionType);

				if (existedReactionType.equals(ReactionType.HATE) && requestReactionType.equals(ReactionType.LIKE)) {
					bookmarkService.addLikeCount(bookmarkId);
				} else {
					bookmarkService.subtractLikeCount(bookmarkId);
				}
			}

			/* 리액션이 존재하지 않은 경우 */
		} else {
			/*리액션 추가*/
			addReaction(profile, bookmark, requestReactionType);
			if (requestReactionType.equals(ReactionType.LIKE)) {
				bookmarkService.addLikeCount(bookmarkId);
			}
		}

		// bookmarkService.updateLikeCount()
	}

	private void addReaction(final Profile profile, final Bookmark bookmark, final ReactionType reactionType) {
		reactionRepository.save(new Reaction(profile, bookmark, reactionType));
	}

	private void cancelReaction(final Reaction reaction) {
		reactionRepository.deleteByProfileAndBookmark(reaction.getProfile(), reaction.getBookmark());
	}

	private void changeReaction(final Reaction reaction, ReactionType requestReactionType) {
		reactionRepository.updateReaction(reaction.getProfile(), reaction.getBookmark(), requestReactionType);
	}
}
