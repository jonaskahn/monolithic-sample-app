package io.github.tuyendev.mbs.common.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class JwtRequestFilter extends GenericFilterBean {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String BEARER_TOKEN_PREFIX = "Bearer ";

	private final JwtTokenProvider tokenProvider;

	public JwtRequestFilter(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		doFilterInternal(request, response, chain);
	}

	private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jwt = resolveToken(httpServletRequest);
		if (StringUtils.hasText(jwt)) {
			this.tokenProvider.authorizeToken(jwt);
		}
		chain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
