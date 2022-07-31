package com.meoguri.linkocean.domain.linkmetadata.service;

import static com.meoguri.linkocean.exception.Preconditions.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Url;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService;
import com.meoguri.linkocean.infrastructure.jsoup.SearchLinkMetadataResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO
// 타이틀 조회 후 삽입 할때 검색(jsoupLinkMetadataService.search) 두번 발생하지 않도록 최적화 c.f. Redis
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LinkMetadataServiceImpl implements LinkMetadataService {

	private final LinkMetadataRepository linkMetadataRepository;
	private final JsoupLinkMetadataService jsoupLinkMetadataService;

	@Override
	public String getTitleByLink(final String link) {
		return linkMetadataRepository.findTitleByUrl(new Url(link))
			.orElseGet(() -> {
				final SearchLinkMetadataResult result = jsoupLinkMetadataService.search(link);
				checkArgument(result.isValid(), "존재하지 않는 url 입니다");

				final LinkMetadata linkMetadata = new LinkMetadata(link, result.getTitle(), result.getImageUrl());
				linkMetadataRepository.save(linkMetadata);

				log.info("save link metadata - url : {}, title : {}", linkMetadata.getSavedUrl(),
					linkMetadata.getTitle());
				return result.getTitle();
			});
	}

	@Override
	public Pageable synchronizeDataAndReturnNextPageable(final Pageable pageable) {

		final Slice<LinkMetadata> slice = linkMetadataRepository.findBy(pageable);
		slice.getContent()
			.forEach(linkMetadata -> {
				final SearchLinkMetadataResult result = jsoupLinkMetadataService.search(linkMetadata.getFullUrl());
				linkMetadata.update(result.getTitle(), result.getImageUrl());
			});
		return slice.hasNext() ? slice.nextPageable() : null;
	}

}
