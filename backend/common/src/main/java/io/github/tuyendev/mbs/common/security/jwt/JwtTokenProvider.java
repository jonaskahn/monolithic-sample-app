package io.github.tuyendev.mbs.common.security.jwt;

public interface JwtTokenProvider {

	JwtAccessToken generateToken(final String username, final String password, final boolean rememberMe);

	JwtAccessToken refreshToken(final String jwtToken);

	void authorizeToken(String jwtToken);

	boolean isSelfIssuer(String jwtToken);
}
