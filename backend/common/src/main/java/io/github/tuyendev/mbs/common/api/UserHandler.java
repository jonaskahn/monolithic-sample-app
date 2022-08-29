package io.github.tuyendev.mbs.common.api;

import io.github.tuyendev.mbs.common.annotation.api.GetRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestHandler(path = "/user", name = "Manage user info, revoked token")
public class UserHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @GetRequest(path = "/info", name = "Show my user information")
    public Response getUserInfo() {
        return Response.ok();
    }
}
