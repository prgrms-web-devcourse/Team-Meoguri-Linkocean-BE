package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class LinkMetadataRepositoryTest extends BasePersistenceTest {

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Test
	void 링크로_제목_조회_성공() {
		//given
		final String saveLink = "naver.com";
		링크_메타데이터_저장(saveLink, "네이버", "naver.png");

		//when
		final Optional<String> oTitle1 = linkMetadataRepository.findTitleByLink(new Link(saveLink));
		final Optional<String> oTitle2 = linkMetadataRepository.findTitleByLink(new Link("unsaved.com"));

		//then
		assertThat(oTitle1).isPresent().get().isEqualTo("네이버");
		assertThat(oTitle2).isEmpty();
	}

	@Test
	void 링크로_조회_성공() {
		//given
		final String saveLink = "naver.com";
		링크_메타데이터_저장(saveLink, "네이버", "naver.png");

		//when
		final Optional<LinkMetadata> oLinkMetadata1 = linkMetadataRepository.findByLink(new Link("naver.com"));
		final Optional<LinkMetadata> oLinkMetadata2 = linkMetadataRepository.findByLink(new Link("unsaved.com"));

		//then
		assertThat(oLinkMetadata1).isPresent();
		assertThat(oLinkMetadata1.get().getLink()).isEqualTo(new Link(saveLink));
		assertThat(oLinkMetadata1.get().getTitle()).isEqualTo("네이버");
		assertThat(oLinkMetadata1.get().getImage()).isEqualTo("naver.png");

		assertThat(oLinkMetadata2).isEmpty();
	}

	@Test
	void 링크_메타데이터_저장_실패_같은_링크_두번_저장() {
		//given
		final String duplicatedLink = "naver.com";
		linkMetadataRepository.save(new LinkMetadata(duplicatedLink, "네이버", "naver.png"));

		assertThatDataIntegrityViolationException()
			.isThrownBy(() -> linkMetadataRepository.save(new LinkMetadata(duplicatedLink, "네이버", "naver.png")));
	}
}
