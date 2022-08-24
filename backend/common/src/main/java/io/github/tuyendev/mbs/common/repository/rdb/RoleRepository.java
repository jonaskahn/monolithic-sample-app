package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

	boolean existsByName(final String name);

	Optional<Role> findRoleByIdAndStatus(Long id, Integer status);

	default Optional<Role> findActiveRoleById(Long id) {
		return findRoleByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}

	Optional<Role> findRoleByNameAndStatus(String name, Integer status);

	default Optional<Role> findActiveRoleByName(String name) {
		return findRoleByNameAndStatus(name, CommonConstants.EntityStatus.ACTIVE);
	}

	Set<Role> findAllByIdInAndStatus(Collection<Long> id, Integer status);

	default Set<Role> findAllActiveByIdIn(final Collection<Long> ids) {
		return findAllByIdInAndStatus(ids, CommonConstants.EntityStatus.ACTIVE);
	}
}
