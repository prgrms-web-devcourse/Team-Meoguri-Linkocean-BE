package com.meoguri.linkocean.domain.linkmetadata.service;

import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.linkmetadata.service.dto.PutLinkMetadataResult;

public interface LinkMetadataService {

	//TODO : getTitleByLink, putLinkMetadataByLink API가 따로 존재할 이유 없는 것 같습니다.
	//현재 비지니스 로직에서는 링크메타데이터 조회 API를 호출할때 링크메타데이터가 존재하면 반환해주고, 존재하지 않으면
	//데이터를 web에서 가져와 db에 저정하고 반환합니다.
	//따라서 하나의 API로 합치는게 맞다는 생각이 듭니다.

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
