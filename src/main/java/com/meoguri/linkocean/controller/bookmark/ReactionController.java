package com.meoguri.linkocean.controller.bookmark;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.internal.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.internal.bookmark.service.ReactionService;
import com.meoguri.linkocean.internal.bookmark.service.dto.ReactionCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class ReactionController {

	private final ReactionService reactionService;

	/**
	 * 사용자는 북마크에 대해 하나의 reaction 만을 가질 수 있다.
	 * 사용자는 reaction 을 등록, 취소, 변경 할 수 있다.
	 * 		like,   hate   요청 reactionType   ->  like,    hate
	 * 등록	 0       0           like 		 	    1 		0
	 * 취소	 1       0           like 		 	    0 		0
	 * 변경	 0       1           like 		 	    1 		0
	 */
	@PostMapping("{bookmarkId}/reactions/{reactionType}")
	public void requestReaction(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId,
		final @PathVariable String reactionType
	) {
		reactionService.requestReaction(
			new ReactionCommand(user.getProfileId(), bookmarkId, ReactionType.of(reactionType)));
	}

}
