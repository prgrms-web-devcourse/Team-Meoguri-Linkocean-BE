package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.domain.bookmark.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping("{bookmarkId}/favorite")
	public void favorite(
		final @LoginUser SessionUser user,
		final @PathVariable long bookmarkId
	) {
		favoriteService.favorite(user.getId(), bookmarkId);
	}

	@PostMapping("{bookmarkId}/unfavorite")
	public void unfavorite(
		final @LoginUser SessionUser user,
		final @PathVariable long bookmarkId
	) {
		favoriteService.unfavorite(user.getId(), bookmarkId);
	}

}
