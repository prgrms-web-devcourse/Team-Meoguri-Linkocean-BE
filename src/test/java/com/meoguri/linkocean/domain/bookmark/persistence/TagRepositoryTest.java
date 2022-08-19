package com.meoguri.linkocean.domain.bookmark.persistence;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class TagRepositoryTest extends BasePersistenceTest {

	@Autowired
	private TagRepository tagRepository;

	@Test
	void 이름으로_조회_성공() {
		//given
		final Tag savedTag = 태그_저장("tag");

		//when
		final Optional<Tag> oFoundTag = tagRepository.findByName("tag");

		//then
		assertThat(oFoundTag).isPresent().get().isEqualTo(savedTag);
	}
}
