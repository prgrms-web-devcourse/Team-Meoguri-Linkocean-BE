package com.meoguri.linkocean.domain.linkmetadata.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 링크의 메타데이터 - 링크에 대한 제목, 이미지 url 을 저장하는 부가정보
 * <li> 링크 메타데이터를 등록할 때 [링크, 제목, 이미지]가 존재해야 한다.</li>
 * <li> 링크 메타데이터는 동일한  [link]에 대해서 [제목, 이미지]를 수정할 수 있다.</li>
 * <li> 여러 사용자가 같은 링크를 북마크 하여도 메타 데이터를 link 에 따라 하나만 저장 된다.</li>
 * <li> 매주 월요일 0시 0분 0초에 스케줄러를 통해 모든 링크 메타 데이터 동기화</li>
 * @see com.meoguri.linkocean.domain.linkmetadata.scheduler.LinkMetadataSynchronizeScheduler
 *
 * <li> Open Graph 프로토콜에 따라 제공되는 링크에 대한 부가 정보를 Jsoup 라이브러리를 통해 조회해와 저장한다.</li>
 * <li> 참고 - <a href="https://ogp.me/"> The Open Graph protocol</li>
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class LinkMetadata extends BaseIdEntity {

	@Embedded
	private Link link;

	//TODO 길이 제약조건 추가하기
	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String image;

	public LinkMetadata(final String link, final String title, final String image) {
		checkNotNull(link);
		checkNotNull(title);
		checkNotNull(image);

		this.link = new Link(link);
		this.title = title;
		this.image = image;
	}

	public void update(final String title, final String image) {
		this.title = title;
		this.image = image;
	}

	public String getFullLink() {
		return link.getFullLink();
	}

	public String getSavedLink() {
		return link.getSavedLink();
	}
}
