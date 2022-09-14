package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.support.domain.persistence.aop.RequireSingleResult;

@RequireSingleResult
public interface FindBookmarkByIdRepository extends Repository<Bookmark, Long> {

	Bookmark findById(long id);

	/* 아이디로 조회 리액션 페치 */
	@Query("select distinct b "
		+ "from Bookmark b "
		+ "left join fetch b.reactions t "
		+ "where b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Bookmark findByIdFetchReactions(long id);

	/* 아이디로 전체 페치 조회 */
	@Query("select distinct b "
		+ "from Bookmark b "
		+ "join fetch b.writer "
		+ "left join fetch b.tagIds t "
		+ "left join fetch b.reactions r "
		+ "where b.id = :id "
		+ "and b.status = com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus.REGISTERED")
	Bookmark findByIdFetchAll(long id);
}
