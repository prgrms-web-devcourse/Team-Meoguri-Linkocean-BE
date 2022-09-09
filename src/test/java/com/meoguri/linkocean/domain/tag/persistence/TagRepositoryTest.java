package com.meoguri.linkocean.domain.tag.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.tag.entity.Tag;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class TagRepositoryTest extends BasePersistenceTest {

	@Autowired
	private TagRepository tagRepository;

	@Test
	void 이름으로_조회_성공() {
		//given
		final Tag savedTag = 태그_저장("tag태그");

		//when
		final Optional<Tag> oFoundTag = tagRepository.findByName(savedTag.getName());

		//then
		assertThat(oFoundTag).isPresent().get().isEqualTo(savedTag);
	}

	@Test
	void 이름으로_조회_대_소문자_구분() {
		//given
		final Tag savedTag = 태그_저장("fun");

		//when
		final Optional<Tag> oFoundTag = pretty(() -> tagRepository.findByName(savedTag.getName().toUpperCase()));

		//then
		assertThat(oFoundTag).isEmpty();
	}

	@Test
	void 태그_집합_목록_조회_성공() {
		//given
		final long tagId1 = 태그_저장("tag1").getId();
		final long tagId2 = 태그_저장("tag2").getId();
		final long tagId3 = 태그_저장("tag3").getId();
		final long tagId4 = 태그_저장("tag4").getId();

		//when
		final List<Set<String>> tagsList = tagRepository.getTagsList(List.of(
			Set.of(tagId1, tagId3, tagId4),
			Set.of(tagId2, tagId3),
			Set.of()
		));

		assertThat(tagsList.get(0)).containsExactlyInAnyOrder("tag1", "tag3", "tag4");
		assertThat(tagsList.get(1)).containsExactlyInAnyOrder("tag2", "tag3");
		assertThat(tagsList.get(2)).isEmpty();
	}
}
