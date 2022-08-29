package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.time.LocalDateTime.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Tags;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  북마크 (인터넷 즐겨찾기)
 *  - 북마크를 등록할 때 [프로필, 링크 메타데이터, 제목, 공개 범위]가 필수로 존재해야 하며 추가로 [메모, 카테고리, url, 태그 목록]를 입력 수 있다.
 *  - 사용자는 [url]당 하나의 북마크를 가질 수 있다.
 *  - 북마크의 [이름. 메모, 카테고리, 공개 범위, 태그 목록]를 수정할 수 있다.
 *  - 북마크를 삭제할 수 있다.
 *  - 북마크의 좋아요 수를 변경할 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Bookmark extends BaseIdEntity {

	public static final int MAX_BOOKMARK_TITLE_LENGTH = 50;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "profile_id")
	private Profile writer;

	@ManyToOne(fetch = LAZY, optional = false)
	private LinkMetadata linkMetadata;

	@Embedded
	private Tags tags;

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
	@Column(nullable = false, length = 700)
	private String url;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	/* 북마크 등록시 사용하는 생성자 */
	public Bookmark(final Profile writer, final LinkMetadata linkMetadata, final String title, final String memo,
		final OpenType openType, final Category category, final String url, final Tags tags) {
		checkNotNull(openType);
		checkNotNull(tags);
		checkNotNull(url);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.writer = writer;
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
		this.tags = tags;
	}

	/* 북마크 제목, 메모, 카테고리, 공개 범위, 북마크 테그를 변경할 수 있다. */
	public void update(final String title, final String memo, final Category category, final OpenType openType,
		final Tags tags) {
		checkNotNull(openType);
		checkNotNull(tags);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.title = title;
		this.memo = memo;
		this.category = category;
		this.openType = openType;
		this.updatedAt = now();
		this.tags = tags;
	}

	public void remove() {
		this.status = BookmarkStatus.REMOVED;
		this.updatedAt = now();
	}

	public List<String> getTagNames() {
		return tags.getTagNames();
	}

	public boolean isOpenTypeAll() {
		return this.openType.equals(OpenType.ALL);
	}
}
