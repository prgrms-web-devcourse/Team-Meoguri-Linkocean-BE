package com.meoguri.linkocean.controller.bookmark;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.ListResponse;
import com.meoguri.linkocean.controller.bookmark.dto.MyTagsResponse;
import com.meoguri.linkocean.domain.bookmark.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@RestController
public class TagController {

	private final TagService tagService;

	@GetMapping
	public ListResponse<MyTagsResponse> getMyTags(@LoginUser SessionUser sessionUser) {

		final List<MyTagsResponse> tags = tagService.getMyTags(sessionUser.getId()).stream()
			.map(MyTagsResponse::ofResult)
			.collect(toList());

		return ListResponse.of("tags", tags);
	}
}
