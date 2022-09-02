package com.meoguri.linkocean.infrastructure.jsoup;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.meoguri.linkocean.domain.linkmetadata.service.dto.GetLinkMetadataResult;

class JsoupGetLinkMetadataServiceTest {

	JsoupGetLinkMetadataService service = new JsoupGetLinkMetadataService();

	@ParameterizedTest
	@CsvSource(value = {
		"https://www.naver.com, 네이버",
		"https://github.com/, GitHub: Where the world builds software"
	})
	void 링크_메타데이터_검색_성공(final String link, final String expectedTitle) {
		//when
		final GetLinkMetadataResult result = service.getLinkMetadata(link);

		//then
		assertThat(result.getTitle()).isEqualTo(expectedTitle);
		assertThat(result.getImage()).isNotNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid link",
		"http://localhost:8080"
	})
	void 링크_메타데이터_검색_실패(final String invalidLink) {
		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> service.getLinkMetadata(invalidLink));
	}

}
