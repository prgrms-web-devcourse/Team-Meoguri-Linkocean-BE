package com.meoguri.linkocean.controller.bookmark.dto;

import static com.meoguri.linkocean.support.controller.dto.Default.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.ProfileResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetDetailedBookmarkResponse {

	private long bookmarkId;
	private String title;
	private String url;

	private String imageUrl;
	private String category;
	private String memo;
	private String openType;

	private Boolean isFavorite;
	private LocalDateTime createdAt;
	private Set<String> tags;
	private Map<ReactionType, Long> reactionCount;
	private Map<ReactionType, Boolean> reaction;
	private GetBookmarkProfileResponse profile;

	public static GetDetailedBookmarkResponse of(final GetDetailedBookmarkResult result) {

		final String category = Optional.ofNullable(Category.toStringKor(result.getCategory())).orElse("no-category");

		return new GetDetailedBookmarkResponse(
			result.getBookmarkId(),
			result.getTitle(),
			result.getUrl(),
			LINK_METADATA_IMAGE.getText(result.getImage()),
			BOOKMARK_CATEGORY.getText(Category.toStringKor(result.getCategory())),
			result.getMemo(),
			OpenType.toString(result.getOpenType()),
			result.isFavorite(),
			result.getCreatedAt(),
			result.getTags(),
			result.getReactionCount(),
			result.getReaction(),
			GetBookmarkProfileResponse.of(result.getProfile())
		);
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	static class GetBookmarkProfileResponse {

		private long profileId;
		private String username;
		private String imageUrl;
		private Boolean isFollow;

		public static GetBookmarkProfileResponse of(final ProfileResult profile) {
			return new GetBookmarkProfileResponse(
				profile.getProfileId(),
				profile.getUsername(),
				profile.getImage(),
				profile.isFollow()
			);
		}
	}

}
