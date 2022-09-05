package com.meoguri.linkocean.domain.linkmetadata.service;

import com.meoguri.linkocean.domain.linkmetadata.service.dto.GetLinkMetadataResult;

public interface GetLinkMetadata {

	GetLinkMetadataResult getLinkMetadata(String url);
}
