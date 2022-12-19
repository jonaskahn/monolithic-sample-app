package io.github.tuyendev.mbs.common.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final JwtTokenProvider jwtTokenProvider;

	private final RestAuthenticationEntryPoint authenticationEntryPoint;

	public JwtSecurityAdapter(JwtTokenProvider jwtTokenProvider, RestAuthenticationEntryPoint authenticationEntryPoint) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void configure(HttpSecurity http) {
		http.addFilterBefore(new SimpleJwtAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint), UsernamePasswordAuthenticationFilter.class);
	}
}
