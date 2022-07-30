package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.bookmark.entity.BookmarkTag;

public interface BookmarkTagRepository extends JpaRepository<BookmarkTag, Long> {
}
