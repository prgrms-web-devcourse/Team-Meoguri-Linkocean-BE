package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.ProfileResult;
import com.meoguri.linkocean.domain.profile.command.entity.vo.ReactionType;

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

		final String openType = OpenType.toString(result.getOpenType());
		final String category = Optional.ofNullable(Category.toStringKor(result.getCategory())).orElse("no-category");

		return new GetDetailedBookmarkResponse(
			result.getBookmarkId(),
			result.getTitle(),
			result.getUrl(),
			result.getImage(),
			category,
			result.getMemo(),
			openType,
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
