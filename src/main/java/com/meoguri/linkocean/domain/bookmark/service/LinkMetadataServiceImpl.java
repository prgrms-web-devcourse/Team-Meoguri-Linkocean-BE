package com.meoguri.linkocean.domain.bookmark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.LinkMetadata;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;
import com.meoguri.linkocean.domain.bookmark.repository.LinkMetadataRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.PutLinkMetadataResult;
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
	@Transactional(readOnly = true)
	public String getTitleByLink(final String link) {
		final String reducedLink = removeSchemaAndWwwIfExists(link);

		// 1. 기존에 존재하는 link metadata 인지 확인
		return linkMetadataRepository.findTitleByUrl(new Url(link))
			.orElseGet(() ->
				// 2. 아니라면 검색해서 반환
				jsoupLinkMetadataService.search(addSchemaAndWww(reducedLink)).getTitle()
			);
	}

	@Override
	public PutLinkMetadataResult putLinkMetadataByLink(final String link) {
		final String reducedLink = removeSchemaAndWwwIfExists(link);

		final Optional<LinkMetadata> oLinkMetadata = linkMetadataRepository.findByUrl(new Url(reducedLink));

		// 1. 기존에 존재하는 link metadata 인지 확인
		if (oLinkMetadata.isPresent()) {
			// 2. 존재 한다면 반환
			return convert(oLinkMetadata.get());
		} else {
			// 3. 새로운 link metadata 검색
			final SearchLinkMetadataResult result = jsoupLinkMetadataService.search(addSchemaAndWww(reducedLink));
			final LinkMetadata linkMetadata = new LinkMetadata(reducedLink, result.getTitle(), result.getImageUrl());

			// 4. 데이터 베이스에 저장
			linkMetadataRepository.save(linkMetadata);
			log.info("save link metadata - url : {}, title : {}",
				Url.toString(linkMetadata.getUrl()), linkMetadata.getTitle());

			// 5. 반환
			return convert(linkMetadata);
		}
	}

	private String addSchemaAndWww(final String reducedLink) {
		return "https://www." + reducedLink;
	}

	private String removeSchemaAndWwwIfExists(final String url) {
		return url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
	}

	private PutLinkMetadataResult convert(final LinkMetadata linkMetadata) {
		return new PutLinkMetadataResult(linkMetadata.getTitle(), linkMetadata.getTitle());
	}

}