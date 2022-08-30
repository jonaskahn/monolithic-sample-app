package io.github.tuyendev.mbs.common.configurer;

import io.github.tuyendev.mbs.common.repository.mongodb.MongoAccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.mongodb.MongoRefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenStore;
import io.github.tuyendev.mbs.common.service.token.MongoJwtTokenStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenProviderConfigurer {

	@Bean
	@ConditionalOnMissingBean
	JwtTokenStore jwtTokenStore(MongoAccessTokenRepository accessTokenRepo, MongoRefreshTokenRepository refreshTokenRepo) {
		return new MongoJwtTokenStore(accessTokenRepo, refreshTokenRepo);
	}
}
