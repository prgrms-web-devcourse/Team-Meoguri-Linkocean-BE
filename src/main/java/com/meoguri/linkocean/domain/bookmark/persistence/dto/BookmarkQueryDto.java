package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookmarkQueryDto {

	private long id;
	private String url;
	private String title;
	private OpenType openType;
	private Category category;
	private LocalDateTime updatedAt;

	private boolean isFavorite;
	private long likeCount;
	private String imageUrl;

	private List<BookmarkTagQueryDto> tagNames;

	public String getOpenType() {
		return openType.getName();
	}

	public String getCategory() {
		return category.getName();
	}

	public List<String> getTagNames() {
		return tagNames.stream()
			.map(BookmarkTagQueryDto::getTagName)
			.collect(Collectors.toList());
	}
}
