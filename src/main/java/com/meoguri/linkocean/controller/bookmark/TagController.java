package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.domain.bookmark.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@RestController
public class TagController {

	private final TagService tagService;

	/**
	 * Tag Controller 는 없어도 될 것 같습니다.
	 * 사용자 태그 목록 조회 API가 따로 필요할 것 같아 초벌로 만들어 두신 것 같지만, 사용자 태그 목록 조회 API가 내 프로필 조회 API에 합쳐지면서 필요없게 됐습니다.
	 */
	// @GetMapping
	// public PageResponse<GetMyTagsResponse> getMyTags(@LoginUser SessionUser sessionUser) {
	//
	// 	final List<GetMyTagsResponse> tags = tagService.getMyTags(sessionUser.getId()).stream()
	// 		.map(GetMyTagsResponse::ofResult)
	// 		.collect(toList());
	//
	// 	return PageResponse.of("tags", tags);
	// }
}
