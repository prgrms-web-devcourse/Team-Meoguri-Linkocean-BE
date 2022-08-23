package com.meoguri.linkocean.domain.profile.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {
	
}
