package io.github.tuyendev.mbs.common.configurer;

import io.github.tuyendev.mbs.common.repository.rdb.AccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.token.DaoJwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
public class JwtTokenProviderConfigurer {

	@Bean
	@ConditionalOnMissingBean
	JwtTokenProvider jwtTokenProvider(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
									  AccessTokenRepository accessTokenRepo, RefreshTokenRepository refreshTokenRepo) {
		return new DaoJwtTokenProvider(authenticationManagerBuilder, userService, accessTokenRepo, refreshTokenRepo);
	}


}
