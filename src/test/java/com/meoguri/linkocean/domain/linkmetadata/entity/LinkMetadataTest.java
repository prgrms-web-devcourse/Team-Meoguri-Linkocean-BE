package com.meoguri.linkocean.domain.linkmetadata.entity;

import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Link;

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
			.extracting(LinkMetadata::getLink, LinkMetadata::getTitle, LinkMetadata::getImage)
			.containsExactly(new Link(link), title, imageUrl);
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
			.extracting(LinkMetadata::getTitle, LinkMetadata::getImage)
			.containsExactly(updatedTitle, updatedImageUrl);
	}
}
