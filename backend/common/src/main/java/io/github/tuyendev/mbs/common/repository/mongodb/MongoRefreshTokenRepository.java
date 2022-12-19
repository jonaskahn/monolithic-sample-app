package io.github.tuyendev.mbs.common.repository.mongodb;

import java.util.Optional;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.mongodb.MongoRefreshToken;

import org.springframework.data.repository.CrudRepository;

public interface MongoRefreshTokenRepository extends CrudRepository<MongoRefreshToken, String> {

	Optional<MongoRefreshToken> findMongoRefreshTokenByIdAfterAndStatus(String id, Integer status);

	default Optional<MongoRefreshToken> findActiveMongoRefreshTokenById(String id) {
		return findMongoRefreshTokenByIdAfterAndStatus(id, CommonConstants.EntityStatus.ACTIVE);
	}

	default void deactivateRefreshTokenById(String id) {
		this.findById(id).ifPresent(mongoRefreshToken -> {
			mongoRefreshToken.setStatus(CommonConstants.EntityStatus.INACTIVE);
			save(mongoRefreshToken);
		});
	}
}
