package com.meoguri.linkocean.domain.bookmark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final ReactionRepository reactionRepository;

	private final BookmarkService bookmarkService;

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Override
	public void requestReaction(ReactionCommand command) {
		final Profile profile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());
		final ReactionType requestReactionType = ReactionType.of(command.getReactionType());

		updateBookmarkReaction(profile, bookmark, requestReactionType);
	}


	private void updateBookmarkReaction(Profile profile, Bookmark bookmark, ReactionType requestReactionType) {
		final Optional<Reaction> oReaction = reactionRepository.findByProfile_idAndBookmark(profile.getId(), bookmark);

		/* 리액션이 존재하는 경우 */
		if (oReaction.isPresent()) {
			final Reaction reaction = oReaction.get();
			ReactionType existedReactionType = ReactionType.of(reaction.getType());

			/*이미 있던 reaction의 reactionType 이 request reactionType 과 같다면*/
			if (existedReactionType.equals(requestReactionType)) {
				cancelReaction(reaction);

				if (existedReactionType.equals(ReactionType.LIKE)) {
					bookmarkService.updateBookmarkLikeCount(bookmark.getId(), -1L);
				}

			/*이미 있던 reaction의 reactionType 이 request reactionType 과 다르다면*/
			} else {
				changeReaction(reaction);

				if (existedReactionType.equals(ReactionType.HATE) && requestReactionType.equals(ReactionType.LIKE)) {
					bookmarkService.updateBookmarkLikeCount(bookmark.getId(), 1L);
				} else {
					bookmarkService.updateBookmarkLikeCount(bookmark.getId(), -1L);
				}
			}

		/* 리액션이 존재하지 않은 경우 */
		} else {
			addReaction(profile, bookmark, requestReactionType);
			if (requestReactionType.equals(ReactionType.LIKE)) {
				bookmarkService.updateBookmarkLikeCount(bookmark.getId(), 1L);
			}
		}



		// oReaction.ifPresentOrElse(
		// 	/* 리액션이 존재하는 경우 */
		// 	reaction -> {
		// 		ReactionType existedReactionType = ReactionType.of(reaction.getType());
		//
		// 		if(existedReactionType.equals())
		// 		/* 기존의 리액션 타입이 요청 리액션 타입과 같은경우 */
		// 		if (existedReactionType == requestReactionType) {
		// 			cancelReaction(profile, bookmark, existedReactionType);
		//
		// 		/* 기존의 리액션이 요청과 다른경우 */
		// 		} else {
		// 			reaction.changeTypeTo(requestReactionType);
		// 		}
		// 	},
		//
		// 	/* 리액션이 존재하지 않은 경우 */
		// 	() -> {
		// 		addReaction(profile, bookmark, requestReactionType);
		// 	}
		// );
	}

	private void addReaction(final Profile profile, final Bookmark bookmark, final ReactionType reactionType) {
		reactionRepository.save(new Reaction(profile, bookmark, reactionType.toString()));
	}

	private void cancelReaction(final Reaction reaction) {
		reactionRepository.deleteByProfileAndBookmarkAndType(
			reaction.getProfile(), reaction.getBookmark(), ReactionType.of(reaction.getType()));
	}

	private void changeReaction(final Reaction reaction) {
		reactionRepository.updateReaction(
			reaction.getProfile(), reaction.getBookmark(), ReactionType.of(reaction.getType()));
	}
}
