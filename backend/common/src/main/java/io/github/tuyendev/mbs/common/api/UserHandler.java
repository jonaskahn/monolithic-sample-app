package io.github.tuyendev.mbs.common.api;

import io.github.tuyendev.mbs.common.annotation.api.GetRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import io.github.tuyendev.mbs.common.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestHandler(path = "/user", name = "Manage user info, revoked token")
public class UserHandler {

	private final UserService userService;

	@GetRequest(path = "/info", name = "Show my user information")
	public Response getUserInfo() {
		return Response.ok();
	}

	@GetRequest(path = "/revoked", desc = "Logout me from all sessions or devices")
	public Response revokeOtherSession() {
		userService.revokeMe();
		return Response.ok();
	}
}
