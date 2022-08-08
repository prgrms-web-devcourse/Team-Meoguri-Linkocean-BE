package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static com.meoguri.linkocean.common.LinkoceanAssert.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;

@Import(FindLinkMetadataByUrlQuery.class)
@DataJpaTest
class FindLinkMetadataByLinkQueryTest {

	@Autowired
	private FindLinkMetadataByUrlQuery findLinkMetadataByUrlQuery;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Test
	void url_이용해_link_metadata_조회_성공() {
		//given
		final LinkMetadata linkMetadata = linkMetadataRepository.save(createLinkMetadata());

		//when
		final LinkMetadata findLinkMetadata =
			findLinkMetadataByUrlQuery.findByUrl(linkMetadata.getSavedLink());

		//then
		assertThat(findLinkMetadata).isEqualTo(linkMetadata);
	}

	@Test
	void 존재하지_않는_url_조회() {
		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> findLinkMetadataByUrlQuery.findByUrl("invalid.com"));
	}
}
