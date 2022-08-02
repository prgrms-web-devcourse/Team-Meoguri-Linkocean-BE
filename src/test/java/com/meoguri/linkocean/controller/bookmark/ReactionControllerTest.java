package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;

import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

class ReactionControllerTest extends BaseControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@BeforeEach
	void setUp() {

	}

	@WithMockUser(roles = "USER")
	@Test
	void 리액션_Api_성공() throws Exception {

		User user1 = userRepository.save( createUser("hani@gmail.com", "GOOGLE") );
		User user2 = userRepository.save( createUser("haha@naver.com", "NAVER") );

		Profile profile1 = profileRepository.save(createProfile(user1, "hani"));
		Profile profile2 = profileRepository.save(createProfile(user2, "gaga"));

		LinkMetadata link = linkMetadataRepository.save(createLinkMetadata());
		Bookmark bookmark2 = bookmarkRepository.save(createBookmark(profile2, link));

		session = new MockHttpSession();
		session.setAttribute("user", new SessionUser(user1));

		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/reactions/{reactionType}",bookmark2.getId(),"like")
			.session(session)
			.contentType(MediaType.APPLICATION_JSON))

			.andExpect(status().isOk())
			.andDo(print());
	}
}
