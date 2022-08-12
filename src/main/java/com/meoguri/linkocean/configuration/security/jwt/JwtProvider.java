package com.meoguri.linkocean.configuration.security.jwt;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String generate(final String email, final String oauthType) {
		final Date now = new Date();
		final Date expiration = new Date(now.getTime() + jwtProperties.getExpiration());

		return Jwts.builder()
			.setSubject("LinkOcean API Token")
			.setIssuer("Meoguri")
			.setIssuedAt(now)
			.setId(email)
			.setAudience(oauthType)
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public <R> R getClaims(final String token, Function<Claims, R> claimsResolver) {
		final Claims claims = parseClaimsJws(token);
		return claimsResolver.apply(claims);
	}

	private Claims parseClaimsJws(final String token) {
		try {
			final String secretKey = jwtProperties.getSecretKey();
			return Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();
		} catch (UnsupportedJwtException e) {
			throw new LinkoceanRuntimeException("the claimsJws argument does not represent an Claims JWS", e);
		} catch (MalformedJwtException e) {
			throw new LinkoceanRuntimeException("the claimsJws string is not a valid JWS", e);
		} catch (SignatureException e) {
			throw new LinkoceanRuntimeException("the claimsJws JWS signature validation fails", e);
		} catch (ExpiredJwtException e) {
			throw new LinkoceanRuntimeException("the specified JWT is a Claims JWT and the Claims "
				+ "has an expiration time before the time this method is invoked.", e);
		} catch (IllegalArgumentException e) {
			throw new LinkoceanRuntimeException("the claimsJws string is null or empty or only whitespace", e);
		}
	}
}
