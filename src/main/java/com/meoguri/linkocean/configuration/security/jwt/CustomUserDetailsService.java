package com.meoguri.linkocean.configuration.security.jwt;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final FindUserByIdQuery findUserByIdQuery;

	@Override
	public UserDetails loadUserByUsername(final String id) {
		final User user = findUserByIdQuery.findById(Long.valueOf(id));

		return new SecurityUser(user.getId(),
			Email.toString(user.getEmail()),
			user.getOAuthType().name(),
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}
}
