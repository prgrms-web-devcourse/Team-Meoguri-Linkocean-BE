package com.meoguri.linkocean.domain.bookmark.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;

class LinkMetadataTest {

	@Test
	void 링크_메타데이터_생성_성공() {

		//given
		final String link = "www.naver.com";
		final String title = "네이버";
		final String imageUrl = "naver.png";

		//when
		final LinkMetadata linkMetadata = new LinkMetadata(link, title, imageUrl);

		//then
		assertThat(linkMetadata).isNotNull()
			.extracting(LinkMetadata::getUrl, LinkMetadata::getTitle, LinkMetadata::getImageUrl)
			.containsExactly(new Url(link), title, imageUrl);
	}
}