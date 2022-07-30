package com.meoguri.linkocean.domain.linkmetadata.service;

import org.springframework.data.domain.Pageable;

public interface LinkMetadataService {

	/**
	 * 링크의 메타 데이터 조회
	 * 1. 메타 데이터가 db에 존재하면 바로 반환
	 * 2. 메타 데이터가 db에 없다면 웹에서 링크 메타데이터를 가져와 db에 저장 후 반환
	 * @throws IllegalArgumentException 유효하지 않은 Link
	 */
	String getTitleByLink(String link);

	/**
	 * 페이지 단위로 링크 메타 데이터를 주기적으로 업데이트 한다.
	 * @return 다음 페이지, 만약 없으면 null 반환
	 */
	Pageable synchronizeDataAndReturnNextPageable(Pageable pageable);
}
