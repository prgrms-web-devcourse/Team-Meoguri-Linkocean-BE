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
import com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	public static final int MAX_TAGS_COUNT = 5;

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
	@Enumerated(STRING)
	private BookmarkStatus status;

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
		final String openType, final String category, final String url, final List<Tag> tags) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.profile = profile;
		this.linkMetadata = linkMetadata;
		this.title = title;
		this.memo = memo;
		this.openType = OpenType.of(openType);
		this.category = Category.of(category);
		this.status = BookmarkStatus.REGISTERED;
		this.url = url;

		setBookmarkTags(tags);
		this.likeCount = 0;
		this.createdAt = now();
		this.updatedAt = now();
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
		setBookmarkTags(tags);
	}

	private void setBookmarkTags(List<Tag> tags) {
		checkCondition(tags.size() <= MAX_TAGS_COUNT);

		this.bookmarkTags = tags.stream()
			.map(tag -> new BookmarkTag(this, tag))
			.collect(toList());
	}

	public void remove() {
		this.status = BookmarkStatus.REMOVED;
		this.updatedAt = now();
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

	public boolean isOwnedBy(final Profile profile) {
		return this.profile.equals(profile);
	}

	/**
	 * 좋아요 수 + 1
	 */
	public void addLikeOne() {
		this.likeCount += 1;
	}

	/**
	 * 좋아요 수 - 1
	 */
	public void subtractLikeOne() {
		this.likeCount -= 1;
	}
}
