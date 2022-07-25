package com.meoguri.linkocean.domain.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByName(String name);

	List<Category> findByNameIn(List<String> names);
}
