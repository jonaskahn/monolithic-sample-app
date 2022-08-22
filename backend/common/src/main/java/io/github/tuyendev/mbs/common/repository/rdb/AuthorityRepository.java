package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Collection;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;

import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {

	Set<Authority> findAllByIdInAndStatus(final Collection<Long> ids, final Integer status);

	default Set<Authority> findAllActiveByIdIn(final Collection<Long> ids) {
		return findAllByIdInAndStatus(ids, CommonConstants.EntityStatus.ACTIVE);
	}

	Set<Authority> findAllByStatus(final Integer status);

	default Set<Authority> findAllActive() {
		return findAllByStatus(CommonConstants.EntityStatus.ACTIVE);
	}
}
