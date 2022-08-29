package com.meoguri.linkocean.domain.bookmark.entity.vo;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class TagIds {

	public static final int MAX_TAGS_COUNT = 5;

	@ElementCollection
	@CollectionTable(
		name = "bookmark_tag",
		joinColumns = @JoinColumn(name = "bookmark_id"),
		uniqueConstraints = @UniqueConstraint(columnNames = {"bookmark_id", "tag_id"})
	)
	@Column(name = "tag_id")
	private Set<Long> values = new HashSet<>();

	public TagIds(final List<Long> values) {
		checkCondition(values.size() <= MAX_TAGS_COUNT, "태그는 %d개 이하여야 합니다", MAX_TAGS_COUNT);

		this.values = new HashSet<>(values);
	}

}
