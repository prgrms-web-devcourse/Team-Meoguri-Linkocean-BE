package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkQueryDto;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface CustomBookmarkRepository {
	List<BookmarkQueryDto> findMyBookmarksUsingSearchCond(Profile profile, MyBookmarkSearchCond searchCond);
}
