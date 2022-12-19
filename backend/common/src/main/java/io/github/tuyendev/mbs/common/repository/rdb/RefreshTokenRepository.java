package io.github.tuyendev.mbs.common.repository.rdb;

import java.util.Optional;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.RefreshToken;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findRefreshTokenByIdAndStatus(final String id, final Integer status);

	default Optional<RefreshToken> findActiveRefreshTokenBy(final String id) {
		return findRefreshTokenByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}

	@Modifying
	@Query("update refresh_tokens rt set rt.status = 0 where rt.id = :id")
	void deactivateRefreshTokenById(@Param("id") String id);
}
