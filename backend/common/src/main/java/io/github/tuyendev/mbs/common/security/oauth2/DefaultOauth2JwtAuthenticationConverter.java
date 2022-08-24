package io.github.tuyendev.mbs.common.security.oauth2;

import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class DefaultOauth2JwtAuthenticationConverter implements Oauth2JwtAuthenticationConverter {

	private final JwtTokenProvider tokenService;

	public DefaultOauth2JwtAuthenticationConverter(JwtTokenProvider tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		return tokenService.authorizeToken(jwt);
	}
}
