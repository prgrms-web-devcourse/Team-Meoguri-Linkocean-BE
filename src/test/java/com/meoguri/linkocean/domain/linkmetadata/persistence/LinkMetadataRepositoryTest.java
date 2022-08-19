package com.meoguri.linkocean.domain.linkmetadata.persistence;

import static com.meoguri.linkocean.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;

@DataJpaTest
class LinkMetadataRepositoryTest {

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Test
	void 링크로_제목_조회_테스트() {
		//given
		linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png"));

		//when
		final Optional<String> naver = linkMetadataRepository.findTitleByLink(new Link("naver.com"));
		final Optional<String> github = linkMetadataRepository.findTitleByLink(new Link("github.com"));

		//then
		assertThat(naver).isPresent().get().isEqualTo("네이버");
		assertThat(github).isEmpty();
	}

	@Test
	void 링크로_조회_테스트() {
		//given
		linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png"));

		//when
		final Optional<LinkMetadata> naver = linkMetadataRepository.findByLink(new Link("naver.com"));
		final Optional<LinkMetadata> github = linkMetadataRepository.findByLink(new Link("github.com"));

		//then
		assertThat(naver).isPresent().get()
			.extracting(LinkMetadata::getLink, LinkMetadata::getTitle, LinkMetadata::getImage)
			.containsExactly(new Link("naver.com"), "네이버", "naver.png");
		assertThat(github).isEmpty();
	}

	@Test
	void 같은_링크_중복_삽입_실패() {
		//given
		linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png"));

		assertThatDataIntegrityViolationException()
			.isThrownBy(() -> linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png")));
	}
}
