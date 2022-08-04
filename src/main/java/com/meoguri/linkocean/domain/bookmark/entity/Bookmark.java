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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 (인터넷 즐겨찾기)
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "url"})
)
public class Bookmark extends BaseIdEntity {

	public static final int MAX_BOOKMARK_TITLE_LENGTH = 50;

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@ManyToOne(fetch = LAZY)
	private LinkMetadata linkMetadata;

	/* BookmarkTag 의 생명주기는 Bookmark 엔티티가 관리 */
	@Getter(NONE)
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
	@ColumnDefault("0")
	private long likeCount;

	/* 사용자가 입력한 url */
	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	/**
	 * 북마크 등록시 사용하는 생성자
	 */
	@Builder
	private Bookmark(final Profile profile, final LinkMetadata linkMetadata, final String title, final String memo,
		final String openType, final String category, final String url) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.profile = profile;
		this.linkMetadata = linkMetadata;
		this.title = title;
		this.memo = memo;
		this.openType = OpenType.of(openType);
		this.category = Category.of(category);
		this.url = url;

		this.likeCount = 0;
		this.createdAt = now();
		this.updatedAt = now();
	}

	/**
	 * Bookmark - BookmarkTag의 연관관계 편의 메서드
	 */
	public void addBookmarkTag(Tag tag) {
		this.bookmarkTags.add(new BookmarkTag(this, tag));
		checkCondition(this.bookmarkTags.size() <= 5);
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
		this.updatedAt = now();
		updateBookmarkTags(tags);
	}

	private void updateBookmarkTags(List<Tag> tags) {
		checkCondition(tags.size() <= 5);

		this.bookmarkTags = tags.stream()
			.map(tag -> new BookmarkTag(this, tag))
			.collect(toList());
	}

	public List<String> getTagNames() {
		return bookmarkTags.stream().map(BookmarkTag::getTagName).collect(toList());
	}

	public String getCategory() {
		return category == null ? null : category.getKorName();
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

		public String getName() {
			return name().toLowerCase();
		}

		static OpenType of(String arg) {
			return OpenType.valueOf(arg.toUpperCase());
		}
	}

	/**
	 * 북마크의 카테고리
	 */
	@Getter
	@RequiredArgsConstructor
	public enum Category {

		SELF_DEVELOPMENT("자기계발"),
		HUMANITIES("인문"),
		POLITICS("정치"),
		SOCIAL("사회"),
		ART("예술"),
		SCIENCE("과학"),
		TECHNOLOGY("기술"),
		IT("IT"),
		HOME("가정"),
		HEALTH("건강"),
		TRAVEL("여행"),
		COOKING("요리");

		private final String korName;

		public static List<String> getEnglishNames() {
			return Arrays.stream(Category.values()).map(v -> v.korName).collect(toList());
		}

		public static List<String> getKoreanNames() {
			return Arrays.stream(Category.values()).map(Category::getKorName).collect(toList());
		}

		public String getKorName() {
			return this.korName;
		}

		public static Category of(String arg) {
			return arg == null ? null : Arrays.stream(Category.values())
				.filter(category -> category.korName.equals(arg))
				.findAny()
				.orElseThrow(LinkoceanRuntimeException::new);
		}
	}
}
