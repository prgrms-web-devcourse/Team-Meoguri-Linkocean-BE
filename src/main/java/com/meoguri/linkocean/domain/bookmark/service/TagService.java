package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyTagsResult;

public interface TagService {

	List<GetMyTagsResult> getMyTags(long userId);
}
