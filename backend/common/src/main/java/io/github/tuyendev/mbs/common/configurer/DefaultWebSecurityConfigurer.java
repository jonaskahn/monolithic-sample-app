package io.github.tuyendev.mbs.common.configurer;


import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.security.DefaultAuthenticationEntryPoint;
import io.github.tuyendev.mbs.common.security.jwt.JwtSecurityAdapter;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.security.oauth2.Oauth2JwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class DefaultWebSecurityConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    private final Oauth2JwtAuthenticationConverter oauth2JwtAuthenticationConverter;

    private final HandlerExceptionResolver resolver;

    public DefaultWebSecurityConfigurer(JwtTokenProvider jwtTokenProvider,
                                        Oauth2JwtAuthenticationConverter oauth2JwtAuthenticationConverter,
                                        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauth2JwtAuthenticationConverter = oauth2JwtAuthenticationConverter;
        this.resolver = resolver;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.cors().and().csrf().disable()
                .headers()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
                .and()
                .frameOptions()
                .sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/webjars/**", "/error/**").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .antMatchers("/auth/token", "/auth/renew-token", "/auth/forgot-password", "/auth/forgot-password-complete").permitAll()
                .antMatchers("/actuator/**").hasAuthority(CommonConstants.Privilege.READ_PRIVILEGE)
                .anyRequest().authenticated();
        http.formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .apply(securityConfigurerAdapter());
        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(oauth2JwtAuthenticationConverter)));
        http.exceptionHandling()
                .authenticationEntryPoint(new DefaultAuthenticationEntryPoint(resolver));
        return http.build();
        // @formatter:on
    }

    private JwtSecurityAdapter securityConfigurerAdapter() {
        return new JwtSecurityAdapter(jwtTokenProvider, new DefaultAuthenticationEntryPoint(resolver));
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
