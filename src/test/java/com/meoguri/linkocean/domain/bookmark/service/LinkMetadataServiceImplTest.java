package com.meoguri.linkocean.domain.bookmark.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService;
import com.meoguri.linkocean.infrastructure.jsoup.SearchLinkMetadataResult;

@Transactional
@SpringBootTest
class LinkMetadataServiceImplTest {

	@Autowired
	private LinkMetadataService linkMetadataService;

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
}
