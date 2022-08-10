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

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;
	private final ReactionRepository reactionRepository;

	@Override
	public void requestReaction(ReactionCommand command) {
		final Profile profile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());
		final ReactionType requestReactionType = ReactionType.of(command.getReactionType());

		updateBookmarkReaction(profile, bookmark, requestReactionType);
		updateBookmarkLikeCount(bookmark);
	}

	private void updateBookmarkReaction(Profile profile, Bookmark bookmark, ReactionType requestReactionType) {
		final Optional<Reaction> oReaction = reactionRepository.findByProfile_idAndBookmark(profile.getId(), bookmark);

		oReaction.ifPresentOrElse(
			/* 리액션이 존재하는 경우 */
			reaction -> {
				ReactionType existedReactionType = ReactionType.of(reaction.getType());

				/* 기존의 리액션 타입이 요청 리액션 타입과 같은경우 */
				if (existedReactionType == requestReactionType) {
					cancelReaction(profile, bookmark, existedReactionType);

					/* 기존의 리액션이 요청과 다른경우 */
				} else {
					reaction.changeTypeTo(requestReactionType);
				}
			},

			/* 리액션이 존재하지 않은 경우 */
			() -> {
				addReaction(profile, bookmark, requestReactionType);
			}
		);
	}

	private void addReaction(final Profile profile, final Bookmark bookmark, final ReactionType reactionType) {
		// 고민 point
		reactionRepository.save(new Reaction(profile, bookmark, reactionType.toString()));
	}

	private void cancelReaction(final Profile profile, final Bookmark bookmark, final ReactionType reactionType) {
		reactionRepository.deleteByProfileAndBookmarkAndType(profile, bookmark, reactionType);
	}

	private void updateBookmarkLikeCount(Bookmark bookmark) {
		final long likeCount = reactionRepository.countReactionByBookmarkAndType(bookmark, ReactionType.LIKE);
		bookmark.changeLikeCount(likeCount);
	}
}
