package io.github.tuyendev.mbs.common.service.token;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.mongodb.MongoAccessToken;
import io.github.tuyendev.mbs.common.entity.mongodb.MongoRefreshToken;
import io.github.tuyendev.mbs.common.repository.mongodb.MongoAccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.mongodb.MongoRefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenStore;
import io.github.tuyendev.mbs.common.utils.DateUtils;

import java.util.Date;

public class MongoJwtTokenStore implements JwtTokenStore {

    private final MongoAccessTokenRepository accessTokenRepo;
    private final MongoRefreshTokenRepository refreshTokenRepo;

    public MongoJwtTokenStore(MongoAccessTokenRepository accessTokenRepo, MongoRefreshTokenRepository refreshTokenRepo) {
        this.accessTokenRepo = accessTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }


    @Override
    public void saveAccessToken(String id, Long userId, Date expiration) {
        MongoAccessToken accessToken = MongoAccessToken.builder()
                .id(id)
                .userId(userId)
                .status(CommonConstants.EntityStatus.ACTIVE)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .build();
        accessTokenRepo.save(accessToken);
    }

    @Override
    public void saveRefreshToken(String id, String accessTokenId, Long userId, Date expiration) {
        MongoRefreshToken refreshToken = MongoRefreshToken.builder()
                .id(id)
                .accessTokenId(accessTokenId)
                .userId(userId)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .build();
        refreshTokenRepo.save(refreshToken);
    }

    @Override
    public Long getUserIdByRefreshTokenId(String refreshTokenId) {
        return refreshTokenRepo.findActiveMongoRefreshTokenById(refreshTokenId)
                .map(MongoRefreshToken::getUserId)
                .orElseThrow(RevokedJwtTokenException::new);
    }

    @Override
    public void inactiveAccessTokenById(String id) {
        this.refreshTokenRepo.deactivateRefreshTokenById(id);
    }

    @Override
    public void inactiveRefreshTokenById(String id) {
        refreshTokenRepo.deactivateRefreshTokenById(id);
    }

    @Override
    public boolean isAccessTokenExisted(String accessTokenId) {
        return accessTokenRepo.existsActiveMongoAccessTokenById(accessTokenId);
    }
}
