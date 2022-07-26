package com.meoguri.linkocean.domain.bookmark.service;

import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;

public interface ReactionService {

	/**
	 * 리액션 추가
	 * @throws javax.persistence.PersistenceException 중복 추가
	 * @throws com.meoguri.linkocean.exception.LinkoceanRuntimeException 예외적인 요청
	 */
	void addReaction(ReactionCommand addReactionCommand);

	/**
	 * 리액션 취소
	 * @throws com.meoguri.linkocean.exception.LinkoceanRuntimeException 예외적인 요청
	 */
	void cancelReaction(ReactionCommand reactionCommand);
}
