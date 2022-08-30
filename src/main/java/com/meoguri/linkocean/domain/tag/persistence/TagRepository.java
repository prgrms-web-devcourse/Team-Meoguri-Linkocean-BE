package com.meoguri.linkocean.domain.tag.persistence;

import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByName(String name);

	@Query("select t.id, t.name "
		+ "from Tag t "
		+ "where t.id in :tagIds ")
	Set<Tuple> getTagsInternal(Set<Long> tagIds);

	/* 태그 집합 목록 조회 */
	default List<Set<String>> getTagsList(List<Set<Long>> tagIdsList) {

		final Set<Long> allTagIds = tagIdsList.stream().flatMap(Collection::stream).collect(toSet());
		final Set<Tuple> tupleSet = getTagsInternal(allTagIds);

		final Map<Long, String> tagMap = tupleSet.stream().collect(toMap(
			tuple -> (Long)tuple.get(0),
			tuple -> (String)tuple.get(1)
		));

		return tagIdsList.stream().map(
			tagIds -> tagIds.stream().map(tagMap::get).collect(toSet())
		).collect(toList());
	}

	/* 태그 아이디 집합으로 태그 집합 조회 */
	default Set<String> getTags(Set<Long> tagIds) {
		return getTagsInternal(tagIds).stream().map(tuple -> (String)tuple.get(1)).collect(toSet());
	}

	/* 태그 아이디 목록으로 태그 목록 조회 */
	default List<String> getTags(List<Long> tagIds) {
		final Set<Tuple> tupleSet = getTagsInternal(new HashSet<>(tagIds));

		final Map<Long, String> tagMap = tupleSet.stream().collect(toMap(
			tuple -> (Long)tuple.get(0),
			tuple -> (String)tuple.get(1)
		));

		return tagIds.stream().map(tagMap::get).collect(toList());
	}
}
