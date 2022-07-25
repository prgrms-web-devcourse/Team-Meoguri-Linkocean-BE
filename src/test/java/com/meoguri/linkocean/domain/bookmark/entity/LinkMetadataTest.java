package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.util.Fixture.*;
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

	@Test
	void 링크_메타데이터_업데이트_성공() {

		//given
		final LinkMetadata linkMetadata = createLinkMetadata();

		final String updatedTitle = "updated title";
		final String updatedImageUrl = "updated image url";

		//when
		linkMetadata.update(updatedTitle, updatedImageUrl);

		//then
		assertThat(linkMetadata)
			.extracting(LinkMetadata::getTitle, LinkMetadata::getImageUrl)
			.containsExactly(updatedTitle, updatedImageUrl);
	}
}
