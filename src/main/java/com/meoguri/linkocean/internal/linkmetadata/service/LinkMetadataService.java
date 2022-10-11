package com.meoguri.linkocean.internal.linkmetadata.service;

import org.springframework.data.domain.Pageable;

public interface LinkMetadataService {

	/**
	 * 링크 제목 조회
	 * 1. 메타 데이터가 db에 존재하면 바로 반환
	 * 2. 메타 데이터가 db에 없다면 웹에서 링크 메타데이터를 가져와 db에 저장 후 반환
	 */
	String obtainTitle(String url);

	/**
	 * 페이지 단위로 링크 메타 데이터를 주기적으로 업데이트 한다
	 * 만약 다음 페이지 없으면 null 반환
	 */
	Pageable synchronizeDataAndReturnNextPageable(Pageable pageable);
}
