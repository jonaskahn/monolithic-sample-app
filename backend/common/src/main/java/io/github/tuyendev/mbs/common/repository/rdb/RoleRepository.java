package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Collection;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

	Set<Role> findAllByIdInAndStatus(Collection<Long> id, Integer status);

	default Set<Role> findAllActiveByIdIn(final Collection<Long> ids) {
		return findAllByIdInAndStatus(ids, CommonConstants.EntityStatus.ACTIVE);
	}
}
