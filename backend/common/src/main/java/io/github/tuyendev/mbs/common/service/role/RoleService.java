package io.github.tuyendev.mbs.common.service.role;

import io.github.tuyendev.mbs.common.entity.rdb.Role;

import java.util.Set;

public interface RoleService {

    Set<Role> findAllActiveByIds(Set<Long> ids);

    Role findAdminRole();
}
