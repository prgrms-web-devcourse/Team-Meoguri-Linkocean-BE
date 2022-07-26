package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.exception.Preconditions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.bookmark.repository.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.GetProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

	private final GetProfileByUserIdQuery getProfileByUserIdQuery;
	private final GetBookmarkByIdQuery getBookmarkByIdQuery;
	private final ReactionRepository reactionRepository;

	@Override
	public void addReaction(final ReactionCommand command) {

		final Profile profile = getProfileByUserIdQuery.getByUserId(command.getUserId());
		final Bookmark bookmark = getBookmarkByIdQuery.GetById(command.getBookmarkId());
		final ReactionType reactionType = ReactionType.valueOf(command.getReactionType());

		reactionRepository.save(new Reaction(profile, bookmark, reactionType));
	}

	@Override
	public void cancelReaction(final ReactionCommand command) {

		final Profile profile = getProfileByUserIdQuery.getByUserId(command.getUserId());
		final Bookmark bookmark = getBookmarkByIdQuery.GetById(command.getBookmarkId());
		final ReactionType reactionType = ReactionType.valueOf(command.getReactionType());

		final boolean isDeleted
			= reactionRepository.deleteByProfileAndBookmarkAndType(profile, bookmark, reactionType) > 0;
		checkCondition(isDeleted);
	}
}
