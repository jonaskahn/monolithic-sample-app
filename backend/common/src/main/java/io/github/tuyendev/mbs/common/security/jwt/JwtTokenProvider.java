package io.github.tuyendev.mbs.common.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtTokenProvider {

	JwtAccessToken generateToken(final String username, final String password, final boolean rememberMe);

	JwtAccessToken refreshToken(final String jwtToken);

	void authorizeToken(String jwtToken);

	boolean isSelfIssuer(String jwtToken);

	AbstractAuthenticationToken authorizeOauth2Token(Jwt jwt);
}
