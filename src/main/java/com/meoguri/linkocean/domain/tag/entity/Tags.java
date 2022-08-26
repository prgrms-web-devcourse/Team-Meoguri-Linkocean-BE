package com.meoguri.linkocean.domain.tag.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.UniqueConstraint;

import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Tags {

	public static final int MAX_TAGS_COUNT = 5;

	@ManyToMany
	@JoinTable(
		name = "bookmark_tag",
		joinColumns = @JoinColumn(name = "bookmark_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id"),
		uniqueConstraints = @UniqueConstraint(columnNames = {"bookmark_id", "tag_id"})
	)
	private Set<Tag> tags = new HashSet<>();

	public Tags(final List<Tag> tags) {
		checkCondition(tags.size() <= MAX_TAGS_COUNT, "태그는 %d개 이하여야 합니다", MAX_TAGS_COUNT);

		this.tags = new HashSet<>(tags);
	}

	public List<String> getTagNames() {
		return tags.stream().map(Tag::getName).collect(toList());
	}
}
