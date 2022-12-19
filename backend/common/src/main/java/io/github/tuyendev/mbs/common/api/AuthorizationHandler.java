package io.github.tuyendev.mbs.common.api;

import javax.validation.Valid;

import io.github.tuyendev.mbs.common.annotation.api.PostRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import io.github.tuyendev.mbs.common.security.jwt.JwtAccessToken;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.token.AuthorizationRequestDto;
import io.github.tuyendev.mbs.common.service.token.RefreshTokenRequestDto;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;

@RestHandler(name = "Authentication handler for generate token", path = "/auth")
@RequiredArgsConstructor
public class AuthorizationHandler {

	private final JwtTokenProvider jwtTokenProvider;

	@PostRequest(name = "Authorization entry point", desc = "Login API, generate token if success", path = "/token")
	public Response<JwtAccessToken> authorize(@Valid @RequestBody AuthorizationRequestDto request) {
		JwtAccessToken token = jwtTokenProvider.generateToken(request.getUsername(), request.getPassword(), request.isRememberMe());
		return Response.ok(token);
	}

	@PostRequest(name = "Renew token entry point", desc = "Refresh the old accessToken, generate token if success", path = "/renew-token")
	public Response<JwtAccessToken> reauthorize(@Valid @RequestBody RefreshTokenRequestDto request) {
		JwtAccessToken token = jwtTokenProvider.renewToken(request.getRefreshToken());
		return Response.ok(token);
	}
}
