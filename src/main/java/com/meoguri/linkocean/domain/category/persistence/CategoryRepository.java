package com.meoguri.linkocean.domain.category.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByNameIn(List<String> names);
}
