package com.meoguri.linkocean.configuration.resolver;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetProfileQueryParams {

	/* 페이징 정보 */
	private final int page;
	private final int size;

	/* 필터링 정보 */
	private final String username;
	
	public Pageable toPageable() {
		/* PageRequest 는 0 부터 페이지를 세기 때문에 조정해줌 */
		return PageRequest.of(page - 1, size);
	}
}
