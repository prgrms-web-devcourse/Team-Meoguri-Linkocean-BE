package com.meoguri.linkocean.infrastructure.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.meoguri.linkocean.internal.linkmetadata.service.GetLinkMetadata;
import com.meoguri.linkocean.internal.linkmetadata.service.dto.GetLinkMetadataResult;

@Service
public class JsoupGetLinkMetadataService implements GetLinkMetadata {

	private static final String CONTENT = "content";

	public static final String DEFAULT_TITLE = "제목 없음";
	public static final String DEFAULT_IMAGE = "default-image.png";

	/**
	 * http:// 혹은 https:// 로 시작하는 url 에 대해서
	 * jsoup 을 활용한 metadata 검색 결과 제공
	 *
	 * @return 조회할 수 있지만 링크 메타데이터가 존재하지 않는 경우. DEFAULT_TITLE, DEFAULT_IMAGE를 반환
	 * @throws IllegalArgumentException 조회할 수 없는 url
	 */
	@Override
	public GetLinkMetadataResult getLinkMetadata(String url) {
		try {
			Document document = Jsoup.connect(url).get();

			final Element titleElement = document.select("meta[property=og:title]").first();
			final Element imageElement = document.select("meta[property=og:image]").first();

			return new GetLinkMetadataResult(getTitle(titleElement), getImage(imageElement));
		} catch (IOException | IllegalArgumentException e) { /* jsoup으로 조회 할 수 없는 url */
			throw new IllegalArgumentException("링크 메타데이터가 존재하지 않습니다.");
		}
	}

	private String getTitle(final Element titleElement) {
		if (titleElement == null) {
			return DEFAULT_TITLE;
		}
		return titleElement.attr(CONTENT).isEmpty() ? DEFAULT_TITLE : titleElement.attr(CONTENT);
	}

	private String getImage(final Element imageElement) {
		if (imageElement == null) {
			return DEFAULT_IMAGE;
		}
		return imageElement.attr(CONTENT).isEmpty() ? DEFAULT_IMAGE : imageElement.attr(CONTENT);
	}
}
