package io.github.tuyendev.mbs.common.service.role;

import java.util.Set;

import io.github.tuyendev.mbs.common.entity.rdb.Role;

public interface RoleService {

	Role findActiveByName(String name);

	Set<Role> findAllActiveByIds(Set<Long> ids);

	Role findAdminRole();
}
