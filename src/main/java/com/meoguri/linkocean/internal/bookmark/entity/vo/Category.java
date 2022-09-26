package com.meoguri.linkocean.internal.bookmark.entity.vo;

import static java.lang.String.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

	public static List<String> getKoreanNames() {
		return Arrays.stream(Category.values()).map(Category::getKorName).collect(toList());
	}

	//질문 getKorName, toStringKor의 역할이 동일한데 하나는 없애도 괜찮지 않을까요?
	public String getKorName() {
		return this.korName;
	}

	public static String toStringKor(Category category) {
		return category == null ? null : category.korName;
	}

	public static Category of(String arg) {
		return arg == null ? null : Arrays.stream(Category.values())
			.filter(category -> category.korName.equals(arg))
			.findAny()
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such category : %s", arg)));
	}

	public static int totalCount() {
		return Category.values().length;
	}
}
