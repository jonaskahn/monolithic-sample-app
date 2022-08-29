package io.github.tuyendev.mbs.common.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.security.SecuredUser;
import io.github.tuyendev.mbs.common.security.SecuredUserDetails;
import io.github.tuyendev.mbs.common.security.jwt.JwtAccessToken;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;

@Slf4j
public abstract class AbstractJwtTokenProvider implements JwtTokenProvider {

	private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();

	protected final AuthenticationManagerBuilder authenticationManagerBuilder;

	protected final UserService userService;

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

	protected AbstractJwtTokenProvider(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userService = userService;
	}


	@PostConstruct
	void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JwtAccessToken generateToken(String username, String password, boolean rememberMe) {
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
		saveAccessToken(accessTokenId, currentUser.getId(), expirationAccessToken);

		final String refreshTokenId = UUID.randomUUID().toString();
		final Date expirationRefreshToken = getExpirationDate(issuedAt, refreshTokenExpirationInMinutes, rememberMe);
		final String refreshToken = createRefreshToken(accessTokenId, refreshTokenId, issuedAt, expirationRefreshToken);

		saveRefreshToken(refreshTokenId, accessTokenId, currentUser.getId(), expirationRefreshToken);
		return JwtAccessToken.builder()
				.type("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accExpiredAt(expirationAccessToken.getTime())
				.refExpiredAt(expirationRefreshToken.getTime())
				.build();
	}

	private Date getExpirationDate(Date issuedAt, long defaultExpiration, boolean rememberMe) {
		return new Date(issuedAt.getTime() + (rememberMe ? (defaultExpiration + rememberMeExpirationInMinutes) : defaultExpiration) * 1000 * 60);
	}

	private String createAccessToken(final String accessTokenId, final SecuredUser user, Date issuedAt, Date expiration) {
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

	protected abstract void saveAccessToken(String id, Long userId, Date expiration);

	private String createRefreshToken(String accessTokenId, String id, Date issuedAt, Date expiration) {
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

	protected abstract void saveRefreshToken(String id, String accessTokenId, Long userId, Date expiration);


	@Override
	@Transactional(rollbackFor = Exception.class)
	public JwtAccessToken renewToken(String jwtToken) {
		Claims claims = getClaims(jwtToken);
		if (!Objects.equals(claims.getIssuer(), issuer)) {
			throw new UnknownIssuerTokenException();
		}
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.REFRESH_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		final Long userId = getUserIdByRefreshTokenId(claims.getId());
		inactiveAccessTokenById(claims.getSubject());
		inactiveRefreshTokenById(claims.getId());
		User user = userService.findActiveUserById(userId);
		setAuthenticationAfterSuccess(user, jwtToken);
		return createToken(isPreviousRefreshTokenRememberMe(claims));
	}

	private Claims getClaims(String jwt) {
		try {
			return jwtParser.parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException |
				 PrematureJwtException | IllegalArgumentException e) {
			log.trace("Invalid JWT jwt", e);
			throw new InvalidJwtTokenException();
		}
	}

	protected abstract Long getUserIdByRefreshTokenId(String refreshTokenId);

	protected abstract void inactiveAccessTokenById(String id);

	protected abstract void inactiveRefreshTokenById(String id);

	private boolean isPreviousRefreshTokenRememberMe(Claims claims) {
		Long realExpiration = Math.abs(claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
		return realExpiration.equals((refreshTokenExpirationInMinutes + rememberMeExpirationInMinutes) * 1000 * 60);
	}

	@Override
	public void authorizeToken(String jwtToken) {
		Claims claims = getClaims(jwtToken);
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.ACCESS_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		if (!isAccessTokenExisted(claims.getId())) {
			throw new RevokedJwtTokenException();
		}
		User user = userService.findActiveUserById(Long.valueOf(claims.getSubject()));
		setAuthenticationAfterSuccess(user, jwtToken);
	}

	protected abstract boolean isAccessTokenExisted(String accessTokenId);

	protected void setAuthenticationAfterSuccess(User user, String jwtToken) {
		UserDetails userDetails = buildPrincipalForRefreshTokenFromUser(user, jwtToken);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	protected UserDetails buildPrincipalForRefreshTokenFromUser(final User user, final String jwt) {
		SecuredUserDetails userDetails = new SecuredUserDetails(user, jwt);
		postCheckUserStatus.check(userDetails);
		return userDetails;
	}


	@Override
	public boolean isSelfIssuer(String jwtToken) {
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
