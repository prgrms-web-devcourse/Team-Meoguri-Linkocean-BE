package com.meoguri.linkocean.infrastructure.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class JsoupLinkMetadataService {

	public static final String DEFAULT_TITLE = "제목 없음";
	public static final String DEFAULT_IMAGE = "default-image.png";

	/**
	 * http:// 혹은 https:// 로 시작하는 url 에 대해서
	 * jsoup 을 활용한 metadata 검색 결과 제공
	 */
	public SearchLinkMetadataResult search(String url) {
		String title = DEFAULT_TITLE;
		String imageUrl = DEFAULT_IMAGE;

		try {
			Document document = Jsoup.connect(url).get();

			final Element titleElement = document.select("meta[property=og:title]").first();
			final Element imageElement = document.select("meta[property=og:image]").first();

			title = titleElement == null ? DEFAULT_TITLE : titleElement.attr("content");
			imageUrl = imageElement == null ? DEFAULT_IMAGE : imageElement.attr("content");

			return new SearchLinkMetadataResult(title, imageUrl);
		} catch (IOException | IllegalArgumentException e) {
			// url 이 올바르지 않은 경우 기본 값으로 결과 반환
			return new SearchLinkMetadataResult(title, imageUrl);
		}
	}
}
