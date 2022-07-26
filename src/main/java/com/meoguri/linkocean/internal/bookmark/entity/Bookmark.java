package com.meoguri.linkocean.internal.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.time.LocalDateTime.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.meoguri.linkocean.internal.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.internal.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.internal.bookmark.entity.vo.Reactions;
import com.meoguri.linkocean.internal.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.support.internal.entity.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  북마크 (인터넷 즐겨찾기)
 *  - 북마크를 등록할 때 [프로필, 제목, 공개 범위, url]가 필수로 존재해야 하며 추가로 [메모, 카테고리, url, 태그 목록]를 입력 수 있다.
 *  - 사용자는 [url]당 하나의 북마크를 가질 수 있다.
 *  - 북마크 url에 링크 메타데이터 값이 없어도 된다.
 *  - 북마크의 [이름. 메모, 카테고리, 공개 범위, 태그 목록]를 수정할 수 있다.
 *  - 북마크를 삭제할 수 있다.
 *  - 북마크의 좋아요 수를 변경할 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "bookmark")
@Entity
public class Bookmark extends BaseIdEntity {

	public static final int MAX_BOOKMARK_TITLE_LENGTH = 50;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "profile_id")
	private Profile writer;

	/* 링크 메타 데이터 식별자 */
	@Column(name = "link_metadata_id")
	private Long linkMetadataId;

	@Embedded
	private Reactions reactions = new Reactions();

	@Embedded
	private TagIds tagIds;

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
	public Bookmark(final Profile writer, final Long linkMetadataId, final String title, final String memo,
		final OpenType openType, final Category category, final String url, final TagIds tagIds) {
		checkNotNull(writer);
		checkNotNull(openType);
		checkNotNull(tagIds);
		checkNotNull(url);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.writer = writer;
		this.linkMetadataId = linkMetadataId;
		this.title = title;
		this.memo = memo;
		this.openType = openType;
		this.category = category;
		this.status = BookmarkStatus.REGISTERED;
		this.url = url;
		this.likeCount = 0;
		this.createdAt = now();
		this.updatedAt = now();
		this.tagIds = tagIds;
	}

	/* 북마크 제목, 메모, 카테고리, 공개 범위, 북마크 테그를 변경할 수 있다. */
	public void update(final String title, final String memo, final Category category, final OpenType openType,
		final TagIds tagIds) {
		checkNotNull(openType);
		checkNotNull(tagIds);
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.title = title;
		this.memo = memo;
		this.category = category;
		this.openType = openType;
		this.updatedAt = now();
		this.tagIds = tagIds;
	}

	public void remove() {
		this.status = BookmarkStatus.REMOVED;
		this.updatedAt = now();
	}

	public boolean isOpenTypeAll() {
		return this.openType.equals(OpenType.ALL);
	}

	/* 리액션 요청 */
	public ReactionType requestReaction(final long profileId, final ReactionType requestType) {
		return reactions.requestReaction(profileId, requestType);
	}

	/* 리액션 카운트 맵 조회 */
	public Map<ReactionType, Long> countReactionGroup() {
		return reactions.countReactionGroup();
	}

	/* 리액션 확인 */
	public Map<ReactionType, Boolean> checkReaction(final long profileId) {
		return reactions.checkReaction(profileId);
	}

	public Optional<Long> getLinkMetadataId() {
		return Optional.ofNullable(linkMetadataId);
	}

	public Set<Long> getTagIds() {
		return tagIds.getValues();
	}
}
