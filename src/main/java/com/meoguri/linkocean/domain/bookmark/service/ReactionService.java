package com.meoguri.linkocean.domain.bookmark.service;

import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;

public interface ReactionService {
	void requestReaction(ReactionCommand command);
}
