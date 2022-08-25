package io.github.tuyendev.mbs.common.api;

import io.github.tuyendev.mbs.common.annotation.api.GetRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import io.github.tuyendev.mbs.common.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestHandler(path = "/userinfo", desc = "Manage user info, revoked token")
public class UserHandler {

	private final UserService userService;

	@GetRequest(name = "Show my user information")
	public Response getUserInfo() {
		return Response.ok();
	}

	@GetRequest(path = "/revoked", desc = "Logout current user to all sessions or devices")
	public Response revokeOtherSession() {
		userService.revokeMe();
		return Response.ok();
	}
}
