package com.meoguri.linkocean.domain.user;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public long saveOrUpdate(final String email, final String oauthType) {
		log.info("user save start email : {} ", email);
		final User user = userRepository.findByEmailAndOAuthType(
				new Email(email), User.OAuthType.of(oauthType.toUpperCase()))
			.orElseGet(() -> {
				final User savedUser = userRepository.save(new User(email, oauthType.toUpperCase()));

				log.info("새로운 사용자 저장 email : {}, oauth type : {}",
					Email.toString(savedUser.getEmail()), savedUser.getOAuthType());

				return savedUser;
			});

		return user.getId();
	}

}
