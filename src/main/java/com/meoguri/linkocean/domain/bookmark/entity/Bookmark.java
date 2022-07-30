package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.time.LocalDateTime.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 (인터넷 즐겨찾기)
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Bookmark extends BaseIdEntity {

	public static final int MAX_BOOKMARK_TITLE_LENGTH = 50;

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@ManyToOne(fetch = LAZY)
	private LinkMetadata linkMetadata;

	/* BookmarkTag의 생명주기는 Bookmark 엔티티가 관리 */
	@OneToMany(mappedBy = "bookmark", cascade = PERSIST, orphanRemoval = true)
	private List<BookmarkTag> bookmarkTags = new ArrayList<>();

	@Column(nullable = true, length = MAX_BOOKMARK_TITLE_LENGTH)
	private String title;

	@Column(nullable = true)
	@Lob
	private String memo;

	@Column(nullable = false, length = MAX_BOOKMARK_TITLE_LENGTH)
	@Enumerated(STRING)
	private OpenType openType;

	@Column(nullable = true, length = 20)
	@Enumerated(STRING)
	private Category category;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	/**
	 * 북마크 등록시 사용하는 생성자
	 */
	@Builder
	private Bookmark(final Profile profile, final LinkMetadata linkMetadata, final String title, final String memo,
		final String category, final String openType) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.profile = profile;
		this.linkMetadata = linkMetadata;
		this.title = title;
		this.memo = memo;
		this.category = Category.of(category);
		this.openType = OpenType.of(openType);
		this.createdAt = now();
		this.updatedAt = now();
	}

	/**
	 * Bookmark - BookmarkTag의 연관관계 편의 메서드
	 */
	public void addBookmarkTag(Tag tag) {
		checkBookmarkTagsSize();

		this.bookmarkTags.add(new BookmarkTag(this, tag));
	}

	/**
	 * 북마크 제목, 메모, 카테고리, 공개 범위, 북마크 테그를 변경할 수 있다.
	 */
	public void update(final String title, final String memo, final String category, final String openType,
		final List<Tag> tags) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.title = title;
		this.memo = memo;
		this.category = Category.of(category);
		this.openType = OpenType.of(openType);
		updateBookmarkTags(tags);
		this.updatedAt = now();
	}

	private void updateBookmarkTags(List<Tag> tags) {
		checkBookmarkTagsSize();

		this.bookmarkTags = tags.stream()
			.map(tag -> new BookmarkTag(this, tag))
			.collect(toList());
	}

	/**
	 * bookmark에는 최대 5개의 태그만 존재한다.
	 */
	private void checkBookmarkTagsSize() {
		if (this.bookmarkTags.size() >= 5) {
			throw new LinkoceanRuntimeException();
		}
	}

	public List<String> getTagNames() {
		return bookmarkTags.stream().map(BookmarkTag::getTagName).collect(toList());
	}

	public String getCategory() {
		return category.getName();
	}

	public String getOpenType() {
		return openType.getName();
	}

	/**
	 * 북마크의 공개 범위
	 */
	public enum OpenType {
		/* 전체공개 */
		ALL,

		/* 팔로워 대상 공개 */
		PARTIAL,

		/* 개인 공개 */
		PRIVATE;

		String getName() {
			return name().toLowerCase();
		}

		static OpenType of(String arg) {
			return OpenType.valueOf(arg.toUpperCase());
		}
	}

	/**
	 * 북마크의 카테고리
	 */
	public enum Category {

		SELF_DEVELOPMENT, //자기계발
		HUMANITIES, //인문
		POLITICS, //정치
		SOCIAL, //사회
		ART, //예술
		SCIENCE, //과학
		TECHNOLOGY, //기술
		IT, //IT
		HOME, //가정
		HEALTH, //건강
		TRAVEL, //여행
		COOKING; //요리

		public static List<String> getAll() {
			return Arrays.stream(Category.values()).map(Category::getName).collect(toList());
		}

		public String getName() {
			return name().toLowerCase();
		}

		public static Category of(String arg) {
			return Category.valueOf(arg.toUpperCase());
		}
	}
}
