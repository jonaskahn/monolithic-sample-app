package io.github.tuyendev.mbs.common.security.jwt;

import io.github.tuyendev.mbs.common.security.DefaultAuthenticationEntryPoint;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    private final DefaultAuthenticationEntryPoint authenticationEntryPoint;

    public JwtSecurityAdapter(JwtTokenProvider jwtTokenProvider, DefaultAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint), UsernamePasswordAuthenticationFilter.class);
    }
}
