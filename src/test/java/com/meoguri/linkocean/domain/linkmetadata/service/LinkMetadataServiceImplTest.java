package com.meoguri.linkocean.domain.linkmetadata.service;

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
import com.meoguri.linkocean.domain.linkmetadata.repository.LinkMetadataRepository;
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
	void 하나_넣고_하나_조회_성공() {
		//given
		linkMetadataService.putLinkMetadataByLink("www.naver.com");

		//when
		final String title = linkMetadataService.getTitleByLink("www.naver.com");

		//then
		assertThat(title).isEqualTo("네이버");
	}

	/**
	 * See LinkMetadataRepositoryTest.같은_링크_중복_삽입_실패
	 */
	@Test
	void 같은거_여러번_넣어도_저장은_한번만() {
		//when then
		assertThatNoException()
			.isThrownBy(() -> {
				linkMetadataService.putLinkMetadataByLink("www.naver.com");
				linkMetadataService.putLinkMetadataByLink("www.naver.com");
				linkMetadataService.putLinkMetadataByLink("http://www.naver.com");
				linkMetadataService.putLinkMetadataByLink("https://www.naver.com");
			});
	}

	@Test
	void 전체_업데이트_성공() {
		//given
		List<LinkMetadata> linkMetadataList = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			linkMetadataList.add(new LinkMetadata(
					String.format("www.naver%d.com", i),
					String.format("title%d", i),
					String.format("imageUrl%d", i)
				)
			);
		}

		linkMetadataRepository.saveAllAndFlush(linkMetadataList);

		final String newTitle = "newTitle";
		final String newImageUrl = "newImageUrl";
		given(jsoupLinkMetadataService.search(anyString()))
			.willReturn(new SearchLinkMetadataResult(newTitle, newImageUrl));

		//when
		final int batchSize = 3;
		linkMetadataService.synchronizeDataAndReturnNextPageable(PageRequest.of(0, batchSize));

		//then
		final List<LinkMetadata> linkMetaDatas = linkMetadataRepository.findAll();
		assertThat(linkMetaDatas)
			.filteredOn("title", newTitle)
			.filteredOn("imageUrl", newImageUrl)
			.hasSize(batchSize);
	}
}
