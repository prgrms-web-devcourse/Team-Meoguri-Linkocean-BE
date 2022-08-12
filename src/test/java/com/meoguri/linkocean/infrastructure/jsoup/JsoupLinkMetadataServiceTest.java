package com.meoguri.linkocean.infrastructure.jsoup;

import static com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class JsoupLinkMetadataServiceTest {

	JsoupLinkMetadataService service = new JsoupLinkMetadataService();

	@ParameterizedTest
	@CsvSource(value = {
		"https://www.naver.com, 네이버",
		"https://github.com/, GitHub: Where the world builds software"
	})
	void 링크_메타데이터_검색_성공(final String link, final String expectedTitle) {
		//when
		final SearchLinkMetadataResult result = service.search(link);

		//then
		assertThat(result.getTitle()).isEqualTo(expectedTitle);
		assertThat(result.getImage()).isNotNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid link",
		"http://localhost:8080"
	})
	void 링크가_잘못된_경우_검색시_기본값_반환(final String invalidLink) {
		//when
		final SearchLinkMetadataResult result = service.search(invalidLink);

		//then
		assertThat(result.getTitle()).isEqualTo(DEFAULT_TITLE);
		assertThat(result.getImage()).isEqualTo(DEFAULT_IMAGE);
	}

}
