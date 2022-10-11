package com.meoguri.linkocean.controller.profile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.internal.profile.command.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping("{bookmarkId}/favorite")
	public void favorite(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		favoriteService.favorite(user.getProfileId(), bookmarkId);
	}

	@PostMapping("{bookmarkId}/unfavorite")
	public void unfavorite(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long bookmarkId
	) {
		favoriteService.unfavorite(user.getProfileId(), bookmarkId);
	}

}
