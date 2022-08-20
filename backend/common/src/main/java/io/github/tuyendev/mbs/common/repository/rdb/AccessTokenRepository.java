package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Optional;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.AccessToken;

import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {

	boolean existsAccessTokenByIdAndStatus(String id, Integer status);

	default boolean existsActiveAccessTokenById(String id) {
		return existsAccessTokenByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}

	Optional<AccessToken> findAccessTokenByIdAndStatus(final String id, final Integer status);

	default Optional<AccessToken> findActiveAccessTokenById(final String id) {
		return findAccessTokenByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}
}
