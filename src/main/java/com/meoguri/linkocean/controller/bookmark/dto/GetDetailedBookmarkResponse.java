package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.GetBookmarkProfileResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetDetailedBookmarkResponse {

	private String title;
	private String url;

	private String imageUrl;
	private String category;
	private String memo;
	private String openType;

	private Boolean isFavorite;
	private LocalDateTime updatedAt;
	private List<String> tags;
	private Map<Reaction.ReactionType, Long> reactionCount;
	private Map<Reaction.ReactionType, Boolean> reaction;
	private GetBookmarkProfileResponse profile;

	public static GetDetailedBookmarkResponse of(final GetDetailedBookmarkResult result) {
		return new GetDetailedBookmarkResponse(
			result.getTitle(),
			result.getUrl(),
			result.getImage(),
			Category.toString(result.getCategory()),
			result.getMemo(),
			OpenType.toString(result.getOpenType()),
			result.isFavorite(),
			result.getUpdatedAt(),
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

		public static GetBookmarkProfileResponse of(final GetBookmarkProfileResult profile) {
			return new GetBookmarkProfileResponse(
				profile.getProfileId(),
				profile.getUsername(),
				profile.getImage(),
				profile.isFollow()
			);
		}
	}

}
