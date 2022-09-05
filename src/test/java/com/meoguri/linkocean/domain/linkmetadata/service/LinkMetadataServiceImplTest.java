package com.meoguri.linkocean.domain.linkmetadata.service;

import static com.meoguri.linkocean.infrastructure.jsoup.JsoupGetLinkMetadataService.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.linkmetadata.service.dto.GetLinkMetadataResult;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

class LinkMetadataServiceImplTest extends BaseServiceTest {

	@Autowired
	private LinkMetadataService linkMetadataService;

	@MockBean
	private GetLinkMetadata getLinkMetadata;

	@BeforeEach
	void setUp() {
		given(getLinkMetadata.getLinkMetadata(anyString()))
			.willReturn(new GetLinkMetadataResult(DEFAULT_TITLE, DEFAULT_IMAGE));

		given(getLinkMetadata.getLinkMetadata("https://www.naver.com"))
			.willReturn(new GetLinkMetadataResult("네이버", "naver.png"));
	}

	@Test
	void 링크_제목_얻기_성공_첫번째_조회() {
		//given
		final String link = "https://www.naver.com";

		//when
		final String title = linkMetadataService.obtainTitle(link);

		//then
		assertThat(title).isEqualTo("네이버");
	}

	@Test
	void 링크_제목_얻기_성공_두번째_조회() {
		//given
		final String url = "https://www.naver.com";
		링크_제목_얻기(url);

		//when
		final String title = linkMetadataService.obtainTitle(url);

		//then
		assertThat(title).isEqualTo("네이버");
	}

	@Test
	void 링크_제목_얻기_성공_유효하지_않은_url() {
		//given
		final String invalidUrl = "https://www.invalid.com";
		given(getLinkMetadata.getLinkMetadata(invalidUrl))
			.willReturn(new GetLinkMetadataResult(DEFAULT_TITLE, DEFAULT_IMAGE));

		//when
		final String title = linkMetadataService.obtainTitle(invalidUrl);

		// then
		assertThat(title).isEqualTo(DEFAULT_TITLE);
	}

	@Test
	void synchronizeDataAndReturnNextPageable_성공() {
		//given
		final int batchSize = 3;
		final Pageable firstPageable = PageRequest.of(0, batchSize);

		final String originalTitle1 = 링크_제목_얻기("www.naver1.com");
		final String originalTitle2 = 링크_제목_얻기("www.naver2.com");
		final String originalTitle3 = 링크_제목_얻기("www.naver3.com");
		final String originalTitle4 = 링크_제목_얻기("www.naver4.com");
		final String originalTitle5 = 링크_제목_얻기("www.naver5.com");

		assertThat(List.of(originalTitle1, originalTitle2, originalTitle3, originalTitle4, originalTitle5))
			.containsExactly(DEFAULT_TITLE, DEFAULT_TITLE, DEFAULT_TITLE, DEFAULT_TITLE, DEFAULT_TITLE);

		네이버_링크_메타데이터_업데이트됨();

		//when 첫번째 synchronizeDataAndReturnNextPageable
		final Pageable secondPageable = linkMetadataService.synchronizeDataAndReturnNextPageable(firstPageable);

		//then
		final String updated1Title1 = 링크_제목_얻기("www.naver1.com");
		final String updated1Title2 = 링크_제목_얻기("www.naver2.com");
		final String updated1Title3 = 링크_제목_얻기("www.naver3.com");
		final String updated1Title4 = 링크_제목_얻기("www.naver4.com");
		final String updated1Title5 = 링크_제목_얻기("www.naver5.com");
		assertThat(List.of(updated1Title1, updated1Title2, updated1Title3, updated1Title4, updated1Title5))
			.containsExactly("네이버짱", "네이버짱", "네이버짱", DEFAULT_TITLE, DEFAULT_TITLE);

		//when 두번째 synchronizeDataAndReturnNextPageable
		final Pageable lastPageable = linkMetadataService.synchronizeDataAndReturnNextPageable(secondPageable);

		//then
		final String updated2Title1 = 링크_제목_얻기("www.naver1.com");
		final String updated2Title2 = 링크_제목_얻기("www.naver2.com");
		final String updated2Title3 = 링크_제목_얻기("www.naver3.com");
		final String updated2Title4 = 링크_제목_얻기("www.naver4.com");
		final String updated2Title5 = 링크_제목_얻기("www.naver5.com");
		assertThat(List.of(updated2Title1, updated2Title2, updated2Title3, updated2Title4, updated2Title5))
			.containsExactly("네이버짱", "네이버짱", "네이버짱", "네이버짱", "네이버짱");

		assertThat(lastPageable).isNull();
	}

	private void 네이버_링크_메타데이터_업데이트됨() {
		final GetLinkMetadataResult updatedResult = new GetLinkMetadataResult("네이버짱", "naver-zzang.png");

		given(getLinkMetadata.getLinkMetadata("www.naver1.com")).willReturn(updatedResult);
		given(getLinkMetadata.getLinkMetadata("www.naver2.com")).willReturn(updatedResult);
		given(getLinkMetadata.getLinkMetadata("www.naver3.com")).willReturn(updatedResult);
		given(getLinkMetadata.getLinkMetadata("www.naver4.com")).willReturn(updatedResult);
		given(getLinkMetadata.getLinkMetadata("www.naver5.com")).willReturn(updatedResult);
	}

	@Test
	void 링크_메타데이터_동기화_성공_유효하지_않은_url_존재() {
		//given
		final String crushUrl = "https://crush.com";

		final String originTitle1 = 링크_제목_얻기("https://www.naver.com");
		final String originTitle2 = 링크_제목_얻기(crushUrl);
		final String originTitle3 = 링크_제목_얻기("https://haha.com");

		assertAll(
			() -> assertThat(originTitle1).isEqualTo("네이버"),
			() -> assertThat(originTitle2).isEqualTo(DEFAULT_TITLE),
			() -> assertThat(originTitle3).isEqualTo(DEFAULT_TITLE)
		);

		링크_메타데이터_업데이트("https://www.naver.com", "네이버 채용", "naver-recruite.png");
		링크_메타데이터_없어짐(crushUrl);
		링크_메타데이터_업데이트("https://haha.com", "하하", "haha.png");

		//when
		linkMetadataService.synchronizeDataAndReturnNextPageable(createPageable());

		em.flush();
		em.clear();

		//then
		assertAll(
			() -> assertThat(링크_제목_얻기("https://www.naver.com")).isEqualTo("네이버 채용"),
			() -> assertThat(링크_제목_얻기(crushUrl)).isNull(),
			() -> assertThat(링크_제목_얻기("https://haha.com")).isEqualTo("하하")
		);
	}

	private void 링크_메타데이터_업데이트(String url, String title, String image) {
		given(getLinkMetadata.getLinkMetadata(url)).willReturn(new GetLinkMetadataResult(title, image));
	}

	private void 링크_메타데이터_없어짐(String url) {
		given(getLinkMetadata.getLinkMetadata(url)).willThrow(new IllegalArgumentException());
	}
}
