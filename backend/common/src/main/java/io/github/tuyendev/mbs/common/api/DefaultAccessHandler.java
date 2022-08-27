package io.github.tuyendev.mbs.common.api;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.annotation.api.GetRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import org.springframework.security.access.prepost.PreAuthorize;

@RestHandler(path = "/")
public class DefaultAccessHandler {

    @GetRequest
    @PreAuthorize("hasAuthority('" + CommonConstants.Privilege.READ_BASIC + "')")
    public Response<String> api() {
        return Response.ok();
    }
}
