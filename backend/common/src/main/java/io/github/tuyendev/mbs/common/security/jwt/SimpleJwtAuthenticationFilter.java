package io.github.tuyendev.mbs.common.security.jwt;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class SimpleJwtAuthenticationFilter extends GenericFilterBean {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String BEARER_TOKEN_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	private final AuthenticationEntryPoint authenticationEntryPoint;

	public SimpleJwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		doFilterInternal(request, response, chain);
	}

	private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jwt = resolveToken(httpServletRequest);
		if (StringUtils.hasText(jwt) && jwtTokenProvider.isSelfIssuer(jwt)) {
			try {
				this.jwtTokenProvider.authorizeToken(jwt);
				chain.doFilter(new HiddenTokenRequestWrapper((HttpServletRequest) request), response);
			}
			catch (AuthenticationException e) {
				SecurityContextHolder.clearContext();
				this.logger.trace("Failed to process authentication request", e);
				this.authenticationEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, e);
			}
		}
		else {
			chain.doFilter(request, response);
		}
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}


	static class HiddenTokenRequestWrapper extends HttpServletRequestWrapper {
		/**
		 * construct a wrapper for this request
		 *
		 * @param request
		 */
		public HiddenTokenRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getHeader(String name) {
			return Objects.equals(name, AUTHORIZATION_HEADER) ? null : super.getHeader(name);
		}

		/**
		 * get the Header names
		 */
		@Override
		public Enumeration<String> getHeaderNames() {
			return super.getHeaderNames();
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			return Objects.equals(name, AUTHORIZATION_HEADER) ? null : super.getHeaders(name);
		}
	}
}
