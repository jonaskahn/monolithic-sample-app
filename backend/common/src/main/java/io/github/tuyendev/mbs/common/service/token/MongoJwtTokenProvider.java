package io.github.tuyendev.mbs.common.service.token;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.mongodb.MongoAccessToken;
import io.github.tuyendev.mbs.common.entity.mongodb.MongoRefreshToken;
import io.github.tuyendev.mbs.common.repository.mongodb.MongoAccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.mongodb.MongoRefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
import io.github.tuyendev.mbs.common.utils.DateUtils;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.Date;

public class MongoJwtTokenProvider extends AbstractJwtTokenProvider implements JwtTokenProvider {

	private final MongoRefreshTokenRepository refreshTokenRepo;

	private final MongoAccessTokenRepository accessTokenRepo;

	protected MongoJwtTokenProvider(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, MongoRefreshTokenRepository refreshTokenRepo, MongoAccessTokenRepository accessTokenRepo) {
		super(authenticationManagerBuilder, userService);
		this.refreshTokenRepo = refreshTokenRepo;
		this.accessTokenRepo = accessTokenRepo;
	}

	@Override
	protected void saveAccessToken(String id, Long userId, Date expiration) {
		MongoAccessToken accessToken = MongoAccessToken.builder()
				.id(id)
				.userId(userId)
				.status(CommonConstants.EntityStatus.ACTIVE)
				.expiredAt(DateUtils.dateToLocalDateTime(expiration))
				.build();
		accessTokenRepo.save(accessToken);
	}

	@Override
	protected void saveRefreshToken(String id, String accessTokenId, Long userId, Date expiration) {
		MongoRefreshToken refreshToken = MongoRefreshToken.builder()
				.id(id)
				.accessTokenId(accessTokenId)
				.userId(userId)
				.expiredAt(DateUtils.dateToLocalDateTime(expiration))
				.build();
		refreshTokenRepo.save(refreshToken);
	}

	@Override
	protected Long getUserIdByRefreshTokenId(String refreshTokenId) {
		return refreshTokenRepo.findActiveMongoRefreshTokenById(refreshTokenId)
				.map(MongoRefreshToken::getUserId)
				.orElseThrow(RevokedJwtTokenException::new);
	}

	@Override
	protected void inactiveAccessTokenById(String id) {
		this.refreshTokenRepo.deactivateRefreshTokenById(id);
	}

	@Override
	protected void inactiveRefreshTokenById(String id) {
		refreshTokenRepo.deactivateRefreshTokenById(id);
	}

	@Override
	protected boolean isAccessTokenExisted(String accessTokenId) {
		return accessTokenRepo.existsActiveMongoAccessTokenById(accessTokenId);
	}
}
