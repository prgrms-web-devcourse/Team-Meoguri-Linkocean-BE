package com.meoguri.linkocean.controller.bookmark;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.domain.bookmark.service.ReactionService;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class ReactionController {

	private final ReactionService reactionService;

	@PostMapping("{bookmarkId}/reactions/{reactionType}")
	public void requestReaction(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId,
		final @PathVariable String reactionType
	) {
		final ReactionCommand command = new ReactionCommand(user.getProfileId(), bookmarkId, reactionType);
		reactionService.requestReaction(command);
	}

}
