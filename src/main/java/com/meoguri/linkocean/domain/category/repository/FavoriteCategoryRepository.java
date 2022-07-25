package com.meoguri.linkocean.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.category.entity.FavoriteCategory;

public interface FavoriteCategoryRepository extends JpaRepository<FavoriteCategory, Long> {
}
