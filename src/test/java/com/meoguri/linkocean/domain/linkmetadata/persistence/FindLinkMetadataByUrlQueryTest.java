package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

@Import(FindLinkMetadataByUrlQuery.class)
@DataJpaTest
class FindLinkMetadataByUrlQueryTest {

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
			findLinkMetadataByUrlQuery.findByUrl(linkMetadata.getSavedUrl());

		//then
		assertThat(findLinkMetadata).isEqualTo(linkMetadata);
	}

	@Test
	void 존재하지_않는_url_조회() {
		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> findLinkMetadataByUrlQuery.findByUrl("invalid.com"));
	}
}
