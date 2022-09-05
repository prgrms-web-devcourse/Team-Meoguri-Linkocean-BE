package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

@Import(FindLinkMetadataByUrlQuery.class)
class FindLinkMetadataByLinkQueryTest extends BasePersistenceTest {

	@Autowired
	private FindLinkMetadataByUrlQuery query;

	@Test
	void url_이용해_link_metadata_조회_성공() {
		//given
		final String saveLink = "naver.com";
		final LinkMetadata savedLinkMetadata = 링크_메타데이터_저장(saveLink, "네이버", "naver.png");

		//when
		final Optional<LinkMetadata> oFoundLinkMetadata = query.findByUrl("naver.com");

		//then
		assertThat(oFoundLinkMetadata).hasValue(savedLinkMetadata);
	}

	@Test
	void 존재하지_않는_url_조회() {
		//when
		final Optional<LinkMetadata> oFoundLinkMetadata = query.findByUrl("invalid.com");

		// then
		assertThat(oFoundLinkMetadata).isEmpty();
	}
}
