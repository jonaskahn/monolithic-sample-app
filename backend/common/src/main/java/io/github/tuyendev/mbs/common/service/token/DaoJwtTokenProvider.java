package io.github.tuyendev.mbs.common.service.token;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.AccessToken;
import io.github.tuyendev.mbs.common.entity.rdb.RefreshToken;
import io.github.tuyendev.mbs.common.repository.rdb.AccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
import io.github.tuyendev.mbs.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.Date;

@Slf4j
public class DaoJwtTokenProvider extends AbstractJwtTokenProvider implements JwtTokenProvider {


    private final AccessTokenRepository accessTokenRepo;

    private final RefreshTokenRepository refreshTokenRepo;


    public DaoJwtTokenProvider(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
                               AccessTokenRepository accessTokenRepo, RefreshTokenRepository refreshTokenRepo) {
        super(authenticationManagerBuilder, userService);
        this.accessTokenRepo = accessTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    protected void saveAccessToken(String id, Long userId, Date expiration) {
        AccessToken accessToken = AccessToken.builder()
                .newEntity()
                .id(id)
                .userId(userId)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .status(CommonConstants.EntityStatus.ACTIVE)
                .build();
        accessTokenRepo.save(accessToken);
    }

    @Override
    protected void saveRefreshToken(String id, String accessTokenId, Long userId, Date expiration) {
        RefreshToken refreshToken = RefreshToken.builder()
                .newEntity()
                .id(id)
                .accessTokenId(accessTokenId)
                .userId(userId)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .status(CommonConstants.EntityStatus.ACTIVE)
                .build();
        refreshTokenRepo.save(refreshToken);
    }

    @Override
    protected Long getUserIdByRefreshTokenId(String refreshTokenId) {
        return refreshTokenRepo.findActiveRefreshTokenBy(refreshTokenId)
                .map(RefreshToken::getUserId)
                .orElseThrow(RevokedJwtTokenException::new);
    }

    @Override
    protected void inactiveAccessTokenById(String id) {
        accessTokenRepo.deactivateAccessTokenById(id);
    }

    @Override
    protected void inactiveRefreshTokenById(String id) {
        refreshTokenRepo.deactivateRefreshTokenById(id);
    }

    @Override
    protected boolean isAccessTokenExisted(String accessTokenId) {
        return accessTokenRepo.existsActiveAccessTokenById(accessTokenId);
    }

}
