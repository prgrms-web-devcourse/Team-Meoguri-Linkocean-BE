package com.meoguri.linkocean.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
