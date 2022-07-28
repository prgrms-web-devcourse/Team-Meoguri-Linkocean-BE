package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.time.LocalDateTime.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
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
public class Bookmark extends BaseIdEntity {

	public static final int MAX_BOOKMARK_TITLE_LENGTH = 50;

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@ManyToOne(fetch = LAZY)
	private LinkMetadata linkMetadata;

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
		final String openType) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.profile = profile;
		this.linkMetadata = linkMetadata;
		this.title = title;
		this.memo = memo;
		this.openType = OpenType.of(openType);
		this.createdAt = now();
		this.updatedAt = now();
	}

	public String getOpenType() {
		return openType.getName();
	}

	/**
	 * 북마크 제목, 메모, 공개 범위를 변경할 수 있다.
	 */
	public void update(final String title, final String memo, final String openType) {
		checkNullableStringLength(title, MAX_BOOKMARK_TITLE_LENGTH, "제목의 길이는 %d보다 작아야 합니다.", MAX_BOOKMARK_TITLE_LENGTH);

		this.title = title;
		this.memo = memo;
		this.openType = OpenType.of(openType);
		this.updatedAt = now();
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

		public String getName() {
			return name().toLowerCase();
		}

		public static Category of(String arg) {
			return Category.valueOf(arg.toUpperCase());
		}
	}
}
