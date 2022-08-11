package com.meoguri.linkocean.domain.bookmark.service;

import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;

public interface ReactionService {

	/**
	 * 사용자는 북마크에 대해 하나의 reaction 만을 가질 수 있다.
	 * 사용자는 reaction 을 등록, 취소, 변경 할 수 있다.
	 * 		like,   hate   요청 reactionType   ->  like,    hate
	 * 등록	 0       0           like 		 	    1 		0
	 * 취소	 1       0           like 		 	    0 		0
	 * 변경	 0       1           like 		 	    1 		0
	 */
	void requestReaction(ReactionCommand command);
}
