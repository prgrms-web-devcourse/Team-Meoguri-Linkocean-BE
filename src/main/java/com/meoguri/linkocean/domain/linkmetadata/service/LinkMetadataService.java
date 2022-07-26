package com.meoguri.linkocean.domain.linkmetadata.service;

import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.linkmetadata.service.dto.PutLinkMetadataResult;

public interface LinkMetadataService {

	/**
	 * 링크를 등록하면 타이틀을 자동완성(?) 해주기 위해 필요한 메서드
	 */
	String getTitleByLink(String link);

	/**
	 * 링크로 링크 메타 데이터 등록 후 결과 조회
	 * 이미 존재 한다면 기존 결과를 조회함
	 */
	PutLinkMetadataResult putLinkMetadataByLink(String link);

	/**
	 * 페이지 단위로 링크 메타 데이터를 주기적으로 업데이트 한다.
	 * @return 다음 페이지, 만약 없으면 null 반환
	 */
	Pageable synchronizeDataAndReturnNextPageable(Pageable pageable);
}
