package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Optional;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findUserByEmail(final String email);

	Optional<User> findUserByUsername(final String username);

	Optional<User> findUserByIdAndStatus(final Long id, final Integer status);

	default Optional<User> findActiveUserById(final Long id) {
		return findUserByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);

	}

	boolean existsUserByUsernameAndStatus(final String username, final Integer status);

	default boolean existsActiveUserByUsername(final String username) {
		return existsUserByUsernameAndStatus(username, CommonConstants.EntityStatus.ACTIVE);
	}

}
