package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

	Optional<Role> findRoleByIdAndStatus(Long id, Integer status);

	default Optional<Role> findActiveRoleById(Long id) {
		return findRoleByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}

	Set<Role> findAllByIdInAndStatus(Collection<Long> id, Integer status);

	default Set<Role> findAllActiveByIdIn(final Collection<Long> ids) {
		return findAllByIdInAndStatus(ids, CommonConstants.EntityStatus.ACTIVE);
	}

	Set<Role> findAllByStatus(Integer status);

	default Set<Role> findAllActive() {
		Set<Role> roles = findAllByStatus(CommonConstants.EntityStatus.ACTIVE);
		for (Role role : roles) {
			if (Objects.nonNull(role.getParentId())) {
				role.setParent(this.findActiveRoleById(role.getParentId()).orElse(null));
			}
		}
		return roles;
	}
}
