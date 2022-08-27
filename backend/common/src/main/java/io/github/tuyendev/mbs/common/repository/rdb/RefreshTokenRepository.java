package io.github.tuyendev.mbs.common.repository.rdb;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


    boolean existsRefreshTokenByIdAndStatus(final String id, final int status);

    default boolean existsRefreshTokenByIdAndStatus(final String id) {
        return existsRefreshTokenByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);

    }

    Optional<RefreshToken> findRefreshTokenByIdAndStatus(final String id, final Integer status);

    default Optional<RefreshToken> findActiveRefreshTokenBy(final String id) {
        return findRefreshTokenByIdAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
    }
}
