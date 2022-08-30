package io.github.tuyendev.mbs.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.security.DomainUserDetailsService;
import io.github.tuyendev.mbs.common.security.SecuredUser;
import io.github.tuyendev.mbs.common.security.SecuredUserDetails;
import io.github.tuyendev.mbs.common.service.token.InvalidAudienceTokenException;
import io.github.tuyendev.mbs.common.service.token.InvalidJwtTokenException;
import io.github.tuyendev.mbs.common.service.token.RevokedJwtTokenException;
import io.github.tuyendev.mbs.common.service.token.UnknownIssuerTokenException;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class DefaultJwtTokenProvider implements JwtTokenProvider {

	private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();

	protected final AuthenticationManagerBuilder authenticationManagerBuilder;

	protected final DomainUserDetailsService userDetailsService;

	protected final JwtTokenStore tokenStore;

	private final UserDetailsChecker postCheckUserStatus = new AccountStatusUserDetailsChecker();

	@Value("${app.common.jwt.issuer}")
	private String issuer;

	@Value("${app.common.jwt.access-token-expiration-in-minutes}")
	private long accessTokenExpirationInMinutes;

	@Value("${app.common.jwt.refresh-token-expiration-in-minutes}")
	private long refreshTokenExpirationInMinutes;

	@Value("${app.common.jwt.remember-me-expiration-in-minutes}")
	private long rememberMeExpirationInMinutes;

	@Value("${app.common.jwt.secret-key}")
	private String jwtSecretKey;

	private JwtParser jwtParser;

	private Key secretKey;

	public DefaultJwtTokenProvider(AuthenticationManagerBuilder authenticationManagerBuilder, DomainUserDetailsService userDetailsService, JwtTokenStore tokenStore) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userDetailsService = userDetailsService;
		this.tokenStore = tokenStore;
	}


	@PostConstruct
	void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
	}

	private static Claims getClaims(final JwtParser jwtParser, final String jwt) {
		try {
			return jwtParser.parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException |
				 PrematureJwtException | IllegalArgumentException e) {
			log.trace("Invalid JWT jwt", e);
			throw new InvalidJwtTokenException();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JwtAccessToken generateToken(final String username, final String password, final boolean rememberMe) {
		Authentication authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return createToken(rememberMe);
	}

	private JwtAccessToken createToken(final boolean rememberMe) {
		SecuredUser currentUser = AppContextUtils.getCurrentLoginUser()
				.orElseThrow(() -> new RuntimeException("This should never happen since the authentication is completed before."));
		final Date issuedAt = new Date();

		final String accessTokenId = UUID.randomUUID().toString();
		final Date expirationAccessToken = getExpirationDate(issuedAt, accessTokenExpirationInMinutes, rememberMe);
		final String accessToken = createAccessToken(accessTokenId, currentUser, issuedAt, expirationAccessToken);
		tokenStore.saveAccessToken(accessTokenId, currentUser.getId(), expirationAccessToken);

		final String refreshTokenId = UUID.randomUUID().toString();
		final Date expirationRefreshToken = getExpirationDate(issuedAt, refreshTokenExpirationInMinutes, rememberMe);
		final String refreshToken = createRefreshToken(accessTokenId, refreshTokenId, issuedAt, expirationRefreshToken);

		tokenStore.saveRefreshToken(refreshTokenId, accessTokenId, currentUser.getId(), expirationRefreshToken);
		return JwtAccessToken.builder()
				.type("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accExpiredAt(expirationAccessToken.getTime())
				.refExpiredAt(expirationRefreshToken.getTime())
				.build();
	}

	private Date getExpirationDate(final Date issuedAt, final long defaultExpiration, final boolean rememberMe) {
		return new Date(issuedAt.getTime() + (rememberMe ? (defaultExpiration + rememberMeExpirationInMinutes) : defaultExpiration) * 1000 * 60);
	}

	private String createAccessToken(final String accessTokenId, final SecuredUser user, final Date issuedAt, final Date expiration) {
		return Jwts.builder()
				.setIssuer(issuer)
				.setId(accessTokenId)
				.setAudience(CommonConstants.TokenAudience.ACCESS_TOKEN)
				.setSubject(user.getPreferredUsername())
				.setIssuedAt(issuedAt)
				.setNotBefore(issuedAt)
				.setExpiration(expiration)
				.claim("aut", user.getAuthorityNames())
				.signWith(secretKey)
				.compact();
	}

	private String createRefreshToken(final String accessTokenId, final String id, final Date issuedAt, final Date expiration) {
		return Jwts.builder()
				.setIssuer(issuer)
				.setId(id)
				.setAudience(CommonConstants.TokenAudience.REFRESH_TOKEN)
				.setSubject(accessTokenId)
				.setIssuedAt(issuedAt)
				.setNotBefore(issuedAt)
				.setExpiration(expiration)
				.signWith(secretKey)
				.compact();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JwtAccessToken renewToken(final String jwtToken) {
		Claims claims = getClaims(jwtParser, jwtToken);
		if (!Objects.equals(claims.getIssuer(), issuer)) {
			throw new UnknownIssuerTokenException();
		}
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.REFRESH_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		final Long userId = tokenStore.getUserIdByRefreshTokenId(claims.getId());
		tokenStore.inactiveAccessTokenById(claims.getSubject());
		tokenStore.inactiveRefreshTokenById(claims.getId());
		setAuthenticationAfterSuccess(userId);
		return createToken(isPreviousRefreshTokenRememberMe(claims));
	}

	private boolean isPreviousRefreshTokenRememberMe(final Claims claims) {
		Long realExpiration = Math.abs(claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
		return realExpiration.equals((refreshTokenExpirationInMinutes + rememberMeExpirationInMinutes) * 1000 * 60);
	}

	@Override
	public void authorizeToken(final String jwtToken) {
		Claims claims = getClaims(jwtParser, jwtToken);
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.ACCESS_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		if (!tokenStore.isAccessTokenExisted(claims.getId())) {
			throw new RevokedJwtTokenException();
		}
		setAuthenticationAfterSuccess(claims.getSubject());
	}

	private void setAuthenticationAfterSuccess(final Object userRef) {
		UserDetails userDetails = buildPrincipalForRefreshTokenFromUser(userRef);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private UserDetails buildPrincipalForRefreshTokenFromUser(final Object userRef) {
		SecuredUserDetails userDetails;
		if (userRef instanceof Long) {
			userDetails = userDetailsService.loadUserByUserId((Long) userRef);
		} else if (userRef instanceof String) {
			userDetails = userDetailsService.loadUserByPreferredUsername((String) userRef);
		} else throw new UsernameNotFoundException("app.user.exception.not-found");
		postCheckUserStatus.check(userDetails);
		return userDetails;
	}


	@Override
	public boolean isSelfIssuer(final String jwtToken) {
		try {
			Base64.Decoder decoder = Base64.getUrlDecoder();
			var payload = decoder.decode(jwtToken.split("\\.")[1]);
			Map<String, String> claims = JACKSON_MAPPER.readValue(payload, Map.class);
			return Objects.equals(claims.get((Claims.ISSUER)), issuer);
		} catch (Exception e) {
			log.error("Cannot parse payload in jwtToken", e);
			throw new InvalidJwtTokenException();
		}
	}
}
