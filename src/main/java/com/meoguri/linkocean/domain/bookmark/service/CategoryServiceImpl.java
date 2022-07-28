package com.meoguri.linkocean.domain.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Override
	public List<String> getAllCategories() {
		return Bookmark.Category.getAll();
	}
}
