package com.meoguri.linkocean.domain.linkmetadata.service;

import static com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService;
import com.meoguri.linkocean.infrastructure.jsoup.SearchLinkMetadataResult;

@Transactional
@SpringBootTest
class LinkMetadataServiceImplTest {

	@Autowired
	private LinkMetadataService linkMetadataService;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@MockBean
	private JsoupLinkMetadataService jsoupLinkMetadataService;

	@BeforeEach
	void setUp() {
		when(jsoupLinkMetadataService.search("https://www.naver.com"))
			.thenReturn(new SearchLinkMetadataResult("네이버", "naver.png"));
	}

	@Test
	void db에_있는_url_링크메타데이터_타이틀_조회_성공() {
		//given
		final String link = "https://www.naver.com";
		linkMetadataRepository.save(new LinkMetadata(link, "네이버", "naver.png"));

		//when
		final String title = linkMetadataService.getTitleByLink(link);

		//then
		assertThat(title).isEqualTo("네이버");
	}

	@Test
	void 새로_저장된_url_링크메타데이터_타이틀_조회_성공() {
		//given
		final String link = "https://www.naver.com";

		//when
		final String title = linkMetadataService.getTitleByLink(link);

		//then
		assertThat(title).isEqualTo("네이버");
		assertThat(linkMetadataRepository.count()).isEqualTo(1L);
	}

	@Test
	void 유효하지_않은_url_링크메타데이터_조회() {
		//given
		final String invalidLink = "https://www.invalid.com";
		given(jsoupLinkMetadataService.search(invalidLink))
			.willReturn(new SearchLinkMetadataResult(DEFAULT_TITLE, DEFAULT_IMAGE));

		//when
		final String title = linkMetadataService.getTitleByLink(invalidLink);

		// then
		assertThat(title).isEqualTo(DEFAULT_TITLE);
	}

	@Test
	void 전체_업데이트_성공() {
		//given
		List<LinkMetadata> linkMetadataList = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			linkMetadataList.add(new LinkMetadata(
				String.format("www.naver%d.com", i),
				String.format("title%d", i),
				String.format("image%d", i)
				)
			);
		}

		linkMetadataRepository.saveAllAndFlush(linkMetadataList);

		final String newTitle = "newTitle";
		final String newImage = "newImage";
		given(jsoupLinkMetadataService.search(anyString()))
			.willReturn(new SearchLinkMetadataResult(newTitle, newImage));

		//when
		final int batchSize = 3;
		linkMetadataService.synchronizeDataAndReturnNextPageable(PageRequest.of(0, batchSize));

		//then
		final List<LinkMetadata> linkMetaDatas = linkMetadataRepository.findAll();
		assertThat(linkMetaDatas)
			.filteredOn("title", newTitle)
			.filteredOn("image", newImage)
			.hasSize(batchSize);
	}
}
