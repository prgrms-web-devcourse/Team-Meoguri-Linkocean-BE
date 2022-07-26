package com.meoguri.linkocean.domain.linkmetadata.service;

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
	 * 모든 링크 메타 데이터를 서비스 정책에 맞춰 주기적으로 업데이트
	 * (대용량 데이터일 수 있으므로 페이지 단위로 배치 처리)
	 */
	void synchronizeAllData(int batchSize);
}
