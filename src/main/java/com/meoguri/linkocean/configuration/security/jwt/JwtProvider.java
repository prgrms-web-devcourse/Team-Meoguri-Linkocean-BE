package com.meoguri.linkocean.configuration.security.jwt;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String generateAccessToken(final Email email, final OAuthType oauthType) {
		final Date now = new Date();
		final Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

		return Jwts.builder()
			.setSubject("LinkOcean API Token")
			.setIssuer("Meoguri")
			.setIssuedAt(now)
			.setId(Email.toString(email))
			.setAudience(oauthType.name())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public String generateRefreshToken(final Long userId) {
		final Date now = new Date();
		final Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

		return Jwts.builder()
			.setSubject("LinkOcean Refresh Token")
			.setIssuer("Meoguri")
			.setIssuedAt(now)
			.setId(String.valueOf(userId))
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public <R> R getClaims(final String token, final Function<Claims, R> claimsResolver) {
		final Claims claims = parseClaimsJwt(token);
		return claimsResolver.apply(claims);
	}

	private Claims parseClaimsJwt(final String token) {
		try {
			final String secretKey = jwtProperties.getSecretKey();
			return Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();
		} catch (IllegalArgumentException e) {
			throw new JwtException("the claimsJws string is null or empty or only whitespace", e);
		}
	}

	public long getRefreshTokenExpiration() {
		return jwtProperties.getRefreshTokenExpiration();
	}
}
