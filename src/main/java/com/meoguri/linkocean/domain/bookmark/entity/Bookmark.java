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

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  북마크 (인터넷 즐겨찾기)
 *
 *  - 북마크를 등록할 때 [프로필, 링크 메타데이터, 제목, 공개 범위]가 필수로 존재해야 하며 추가로 [메모, 카테고리, url, 태그 목록]를 입력 수 있다.
 *  - 사용자는 [url]당 하나의 북마크를 가질 수 있다.
 *  - 북마크의 [이름. 메모, 카테고리, 공개 범위, 태그 목록]를 수정할 수 있다.
 *  - 북마크를 삭제할 수 있다.
 *  - 북마크의 좋아요 수를 변경할 수 있다.
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
	@OneToMany(mappedBy = "bookmark", cascade = ALL, orphanRemoval = true)
	private List<BookmarkTag> bookmarkTags = new ArrayList<>();

	@Column(nullable = true, length = MAX_BOOKMARK_TITLE_LENGTH)
	private String title;

	@Column(nullable = true)
	@Lob
	private String memo;

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
	public Bookmark(final Profile profile, final LinkMetadata linkMetadata, final String title, final String memo,
		final OpenType openType, final Category category, final String url, final List<Tag> tags) {
		checkNotNull(openType);
		checkNotNull(tags);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.profile = profile;
		this.linkMetadata = linkMetadata;
		this.title = title;
		this.memo = memo;
		this.openType = openType;
		this.category = category;
		this.status = BookmarkStatus.REGISTERED;
		this.url = url;
		this.likeCount = 0;
		this.createdAt = now();
		this.updatedAt = now();
		setBookmarkTags(tags);
	}

	/**
	 * 북마크 제목, 메모, 카테고리, 공개 범위, 북마크 테그를 변경할 수 있다.
	 */
	public void update(final String title, final String memo, final Category category, final OpenType openType,
		final List<Tag> tags) {
		checkNotNull(category);
		checkNotNull(openType);
		checkNotNull(tags);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.title = title;
		this.memo = memo;
		this.category = category;
		this.openType = openType;
		this.updatedAt = now();
		setBookmarkTags(tags);
	}

	private void setBookmarkTags(List<Tag> tags) {
		checkCondition(tags.size() <= MAX_TAGS_COUNT, "태그는 %d개 이하여야 합니다", MAX_TAGS_COUNT);

		this.bookmarkTags.clear();
		tags.forEach(tag -> bookmarkTags.add(new BookmarkTag(this, tag)));
	}

	public void remove() {
		this.status = BookmarkStatus.REMOVED;
		this.updatedAt = now();
	}

	public List<String> getTagNames() {
		return bookmarkTags.stream().map(BookmarkTag::getTagName).collect(toList());
	}

	public void changeLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

}
