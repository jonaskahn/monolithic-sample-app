package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Optional;
import java.util.Set;

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

	/**
	 * Inactive AccessToken will be inactive the connected RefreshToken too.
	 *
	 * @param id
	 */
	default void inactiveAccessToken(String id) {
		this.findById(id).ifPresent(accessToken ->
				{
					accessToken.setStatus(CommonConstants.EntityStatus.INACTIVE);
					accessToken.getRefreshToken().setStatus(CommonConstants.EntityStatus.INACTIVE);
					this.save(accessToken);
				}
		);
	}

	Set<AccessToken> findAllByUserIdAndStatus(Long userId, Integer status);

	default Set<AccessToken> findAllActiveByUserId(Long userId) {
		return findAllByUserIdAndStatus(userId, CommonConstants.EntityStatus.ACTIVE);
	}
}
