package com.meoguri.linkocean.domain.tag.persistence;

import static java.util.stream.Collectors.*;

import java.util.Collection;
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
	Set<Tuple> getTagsListInternal(Set<Long> tagIds);

	/* 태그 집합 목록 조회 */
	default List<Set<String>> getTagsList(List<Set<Long>> tagIdsList) {

		final Set<Long> allTagIds = tagIdsList.stream().flatMap(Collection::stream).collect(toSet());
		final Set<Tuple> tupleSet = getTagsListInternal(allTagIds);

		final Map<Long, String> tagMap = tupleSet.stream().collect(toMap(
			tuple -> (Long)tuple.get(0),
			tuple -> (String)tuple.get(1)
		));

		return tagIdsList.stream().map(
			tagIds -> tagIds.stream().map(tagMap::get).collect(toSet())
		).collect(toList());
	}
}
