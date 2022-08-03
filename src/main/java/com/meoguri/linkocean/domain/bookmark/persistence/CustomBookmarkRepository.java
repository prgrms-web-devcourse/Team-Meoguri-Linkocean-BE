package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface CustomBookmarkRepository {

	long countByCategory(Profile profile, Category of, String searchTitle);

	List<Bookmark> searchByCategory(Profile profile, Category of, String searchTitle, String order, int page, int size);

	long countByFavorite(Profile profile, boolean favorite, String searchTitle);

	List<Bookmark> searchByFavorite(Profile profile, boolean favorite, String searchTitle, String order, int page,
		int size);

	long countByTags(Profile profile, List<String> tags, String searchTitle);

	List<Bookmark> searchByTags(Profile profile, List<String> tags, String searchTitle, String order, int page,
		int size);

	long countByProfile(Profile profile, String searchTitle);

	List<Bookmark> searchByProfile(Profile profile, String searchTitle, String order, int page, int size);
}
