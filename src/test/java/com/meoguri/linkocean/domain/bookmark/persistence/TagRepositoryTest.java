package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.Tag;

@DataJpaTest
class TagRepositoryTest {

	@Autowired
	private TagRepository tagRepository;

	@Test
	void 이름으로_태그_조회_하기() {
		//given
		final Tag tag = createTag();
		final Tag savedTag = tagRepository.save(tag);

		//when
		final Optional<Tag> retrievedTag = tagRepository.findByName(tag.getName());

		//then
		assertThat(retrievedTag).isPresent().get().isEqualTo(savedTag);
	}
}
