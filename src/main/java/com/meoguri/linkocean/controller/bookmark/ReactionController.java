package com.meoguri.linkocean.controller.bookmark;

import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.service.ReactionService;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class ReactionController {

	private final ReactionService reactionService;

	@PostMapping("{bookmarkId}/reactions/{reactionType}")
	public void requestReaction(
		final @LoginUser SessionUser user,
		final @PathVariable long bookmarkId,
		final @PathVariable String reactionType
	) {
		final ReactionCommand command = new ReactionCommand(user.getId(), bookmarkId, reactionType);
		reactionService.requestReaction(command);
	}

}
