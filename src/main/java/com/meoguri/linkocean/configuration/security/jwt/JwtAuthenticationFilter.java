package com.meoguri.linkocean.configuration.security.jwt;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByEmailAndTypeQuery;
import com.meoguri.linkocean.util.Tokens;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService customUserDetailsService;
	private final FindUserByEmailAndTypeQuery findUserByEmailAndTypeQuery;
	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
		final FilterChain filterChain)
		throws IOException, ServletException {
		try {
			// 토큰 가져오기
			final String token = Tokens.get(request);

			// 토큰이 비었는지 확인
			if (Tokens.isBlankToken(token)) {
				filterChain.doFilter(request, response);
				return;
			}

			final String email = jwtProvider.getClaims(token, Claims::getId);
			final String oauthType = jwtProvider.getClaims(token, Claims::getAudience);

			// email과 OauthType으로 User가 존재하는지 확인
			final User user = findUserByEmailAndTypeQuery.findUserByUserAndType(
				new Email(email),
				User.OAuthType.of(oauthType)
			);

			// @AuthenticalPrincipal을 위한 UserDetails
			final UserDetails userDetails =
				customUserDetailsService.loadUserByUsername(String.valueOf(user.getId()));

			if (isNotExistsUserDetails(userDetails)) {
				throw new IllegalStateException("유효하지 않은 인증정보 입니다.");
			}

			new AccountStatusUserDetailsChecker().check(userDetails);

			// UsernamePasswordAuthenticationToken 만들어서 저장
			final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private boolean isNotExistsUserDetails(final UserDetails userDetails) {
		return Objects.isNull(userDetails);
	}
}
