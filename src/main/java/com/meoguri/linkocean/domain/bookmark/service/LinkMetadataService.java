package com.meoguri.linkocean.domain.bookmark.service;

import com.meoguri.linkocean.domain.bookmark.service.dto.PutLinkMetadataResult;

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
}
