package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

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
		final LinkMetadata foundLinkMetadata = query.findByUrl("naver.com");

		//then
		assertThat(foundLinkMetadata).isEqualTo(savedLinkMetadata);
	}

	@Test
	void 존재하지_않는_url_조회() {
		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> query.findByUrl("invalid.com"));
	}
}
