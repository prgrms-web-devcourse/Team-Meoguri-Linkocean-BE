package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class FindLinkMetadataRepositoryTest extends BasePersistenceTest {

	@Autowired
	private FindLinkMetadataRepository findLinkMetadataRepository;

	@Test
	void 링크로_조회_성공() {
		//given
		final String saveLink = "naver.com";
		링크_메타데이터_저장(saveLink, "네이버", "naver.png");

		//when
		final LinkMetadata linkMetadata1 = findLinkMetadataRepository.findByLink(new Link("naver.com"));
		final LinkMetadata linkMetadata2 = findLinkMetadataRepository.findByLink(new Link("unsaved.com"));

		//then
		assertThat(linkMetadata1.getLink()).isEqualTo(new Link(saveLink));
		assertThat(linkMetadata1.getTitle()).isEqualTo("네이버");
		assertThat(linkMetadata1.getImage()).isEqualTo("naver.png");

		assertThat(linkMetadata2).isNull();
	}

	@Test
	void 링크로_조회_성공_유효하지_않으면_null() {
		//given
		final Link invalidLink = new Link("invalid.com");

		//when
		final LinkMetadata linkMeatadata = findLinkMetadataRepository.findByLink(invalidLink);

		// then
		assertThat(linkMeatadata).isNull();
	}

	@Test
	void 링크_메타데이터_집합_조회() {
		//given
		final Long id1 = 링크_메타데이터_저장("https://www.naver.com", "네이버", "naver.png").getId();
		final Long id2 = 링크_메타데이터_저장("https://www.google.com", "구글", "google.png").getId();
		final Long id3 = 링크_메타데이터_저장("https://www.kakao.com", "카카오", "kakao.png").getId();

		//when
		final Set<LinkMetadata> linkMetaDataSet = findLinkMetadataRepository.findByIds(List.of(id1, id2, id3));

		//then
		assertThat(linkMetaDataSet)
			.extracting(LinkMetadata::getId)
			.containsExactlyInAnyOrder(id1, id2, id3);
	}

}
