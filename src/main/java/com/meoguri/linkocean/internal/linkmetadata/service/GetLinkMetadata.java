package com.meoguri.linkocean.internal.linkmetadata.service;

import com.meoguri.linkocean.internal.linkmetadata.service.dto.GetLinkMetadataResult;

public interface GetLinkMetadata {

	GetLinkMetadataResult getLinkMetadata(String url);
}
