package com.meoguri.linkocean.configuration.security.jwt;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static org.springframework.http.HttpHeaders.*;

import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.meoguri.linkocean.domain.user.domain.UserService;
import com.meoguri.linkocean.domain.user.domain.dto.GetUserResult;
import com.meoguri.linkocean.domain.user.domain.model.Email;
import com.meoguri.linkocean.domain.user.domain.model.OAuthType;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserService userService;

	private final JwtProvider jwtProvider;
	private final UserDetailsChecker userDetailsChecker;

	public JwtAuthenticationFilter(final UserService userService, final JwtProvider jwtProvider) {
		this.userService = userService;
		this.jwtProvider = jwtProvider;
		this.userDetailsChecker = new AccountStatusUserDetailsChecker();
	}

	@Override
	protected void doFilterInternal(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final FilterChain filterChain
	) {
		try {
			/* 토큰 가져오기 */
			final String token = StringUtils.substringAfter(request.getHeader(AUTHORIZATION), "Bearer ");

			/* 토큰이 비었는지 확인 */
			if (StringUtils.isBlank(token)) {
				filterChain.doFilter(request, response);
				return;
			}
			final String email = jwtProvider.getClaims(token, Claims::getId);
			final String oauthType = jwtProvider.getClaims(token, Claims::getAudience);

			/* @AuthenticationPrincipal 을 위한 UserDetails */
			final GetUserResult user = userService.getUser(new Email(email), OAuthType.of(oauthType));
			final UserDetails userDetails = new SecurityUser(
				user.getId(),
				user.getProfileId(),
				user.getEmail(),
				user.getOauthType(),
				List.of(new SimpleGrantedAuthority("ROLE_USER"))
			);
			checkUserDetails(userDetails);

			/* UsernamePasswordAuthenticationToken 만들어서 저장 */
			final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void checkUserDetails(final UserDetails userDetails) {
		checkState(userDetails != null, "유효하지 않은 인증정보 입니다.");
		userDetailsChecker.check(userDetails);
	}
}
