package io.github.tuyendev.mbs.common.api;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.annotation.api.GetRequest;
import io.github.tuyendev.mbs.common.annotation.api.RestHandler;
import io.github.tuyendev.mbs.common.response.Response;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestHandler(name = "RoleHierarchyHandler", path = "/role-hierarchy")
public class RoleHierarchyHandler {

    @GetRequest("/all")
    @PreAuthorize("hasAuthority('" + CommonConstants.Privilege.READ_PRIVILEGE + "')")
    public Response<List> viewRoles() {
        return null;
    }
}
