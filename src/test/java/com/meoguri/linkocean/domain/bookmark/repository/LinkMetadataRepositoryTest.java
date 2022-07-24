package com.meoguri.linkocean.domain.bookmark.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.LinkMetadata;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;

@DataJpaTest
class LinkMetadataRepositoryTest {

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Test
	void 링크로_제목_조회_테스트() {
		//given
		linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png"));

		//when
		final Optional<String> naver = linkMetadataRepository.findTitleByUrl(new Url("naver.com"));
		final Optional<String> github = linkMetadataRepository.findTitleByUrl(new Url("github.com"));

		//then
		assertThat(naver).isPresent().get().isEqualTo("네이버");
		assertThat(github).isEmpty();
	}

	@Test
	void 링크로_조회_테스트() {
		//given
		linkMetadataRepository.save(new LinkMetadata("naver.com", "네이버", "naver.png"));

		//when
		final Optional<LinkMetadata> naver = linkMetadataRepository.findByUrl(new Url("naver.com"));
		final Optional<LinkMetadata> github = linkMetadataRepository.findByUrl(new Url("github.com"));

		//then
		assertThat(naver).isPresent().get()
			.extracting(LinkMetadata::getUrl, LinkMetadata::getTitle, LinkMetadata::getImageUrl)
			.containsExactly(new Url("naver.com"), "네이버", "naver.png");
		assertThat(github).isEmpty();
	}
}