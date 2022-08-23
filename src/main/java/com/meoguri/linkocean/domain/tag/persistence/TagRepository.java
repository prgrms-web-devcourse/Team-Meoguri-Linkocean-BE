package com.meoguri.linkocean.domain.tag.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByName(String name);
}
