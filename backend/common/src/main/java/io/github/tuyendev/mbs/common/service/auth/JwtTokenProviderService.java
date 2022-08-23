package io.github.tuyendev.mbs.common.service.auth;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.AccessToken;
import io.github.tuyendev.mbs.common.entity.rdb.RefreshToken;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.AccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RefreshTokenRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import io.github.tuyendev.mbs.common.security.DomainUserDetails;
import io.github.tuyendev.mbs.common.security.SecurityUserInfoProvider;
import io.github.tuyendev.mbs.common.security.jwt.JwtAccessToken;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;
import io.github.tuyendev.mbs.common.utils.DateUtils;
import io.github.tuyendev.mbs.common.utils.PasswordGeneratorUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class JwtTokenProviderService implements JwtTokenProvider {

	private static final ObjectMapper JACKSON_MAPPER;

	private final JwtParser jwtParser;

	private final Key secretKey;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final PasswordEncoder passwordEncoder;

	private final SecurityUserInfoProvider securityUserInfoProvider;

	private final UserService userService;

	private final AccessTokenRepository accessTokenRepo;

	private final RefreshTokenRepository refreshTokenRepo;

	private final UserRepository userRepo;

	private final RoleRepository roleRepo;

	@Value("${app.common.jwt.issuer}")
	private String issuer;

	@Value("${app.common.jwt.access-token-expiration-in-minutes}")
	private long accessTokenExpirationInMinutes;

	@Value("${app.common.jwt.refresh-token-expiration-in-minutes}")
	private long refreshTokenExpirationInMinutes;

	@Value("${app.common.jwt.remember-me-expiration-in-minutes}")
	private long rememberMeExpirationInMinutes;

	public JwtTokenProviderService(@Value("${app.common.jwt.secret-key}") String jwtSecretKey,
			AuthenticationManagerBuilder authenticationManagerBuilder, PasswordEncoder passwordEncoder, SecurityUserInfoProvider securityUserInfoProvider, UserService userService,
			AccessTokenRepository accessTokenRepo, RefreshTokenRepository refreshTokenRepo, UserRepository userRepo, RoleRepository roleRepo) {
		this.passwordEncoder = passwordEncoder;
		this.securityUserInfoProvider = securityUserInfoProvider;
		this.userService = userService;
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
	}


	@Override
	public JwtAccessToken generateToken(String username, String password, boolean rememberMe) {
		Authentication authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return createToken(rememberMe);
	}

	private JwtAccessToken createToken(final boolean rememberMe) {
		User currentUser = AppContextUtils.getCurrentLoginUser()
				.orElseThrow(() -> new RuntimeException("This should never happen since the authentication is completed before."));
		final Date issuedAt = new Date();
		AccessToken accessToken = createAccessToken(currentUser, issuedAt, rememberMe);
		RefreshToken refreshToken = createRefreshToken(accessToken.getId(), issuedAt, rememberMe);
		accessToken.setRefreshToken(refreshToken);
		accessTokenRepo.save(accessToken);
		return createJwtAccessToken(accessToken.getToken(), refreshToken.getToken(),
				accessToken.getExpiredAt(), refreshToken.getExpiredAt());
	}

	private AccessToken createAccessToken(final User user, Date issuedAt, boolean rememberMe) {
		final Date expiration = getExpirationDate(issuedAt, accessTokenExpirationInMinutes, rememberMe);
		final String id = UUID.randomUUID().toString();
		final String token = Jwts.builder()
				.setIssuer(issuer)
				.setId(id)
				.setAudience(CommonConstants.TokenAudience.ACCESS_TOKEN)
				.setSubject(user.getPreferredUsername())
				.setIssuedAt(issuedAt)
				.setNotBefore(issuedAt)
				.setExpiration(expiration)
				.claim("aut", user.getAuthorities())
				.signWith(secretKey)
				.compact();
		return AccessToken.builder()
				.id(id)
				.userId(user.getId())
				.token(token)
				.expiredAt(DateUtils.dateToLocalDateTime(expiration))
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
	}

	private Date getExpirationDate(Date issuedAt, long defaultExpiration, boolean rememberMe) {
		return new Date(issuedAt.getTime() + (rememberMe ? (defaultExpiration + rememberMeExpirationInMinutes) : defaultExpiration) * 1000 * 60);
	}

	private RefreshToken createRefreshToken(final String accessTokenId, Date issuedAt, boolean rememberMe) {
		Date expiration = getExpirationDate(issuedAt, refreshTokenExpirationInMinutes, rememberMe);
		final String id = UUID.randomUUID().toString();
		final String token = Jwts.builder()
				.setIssuer(issuer)
				.setId(id)
				.setAudience(CommonConstants.TokenAudience.REFRESH_TOKEN)
				.setSubject(accessTokenId)
				.setIssuedAt(issuedAt)
				.setNotBefore(issuedAt)
				.setExpiration(expiration)
				.signWith(secretKey)
				.compact();
		return RefreshToken.builder()
				.id(id)
				.status(CommonConstants.EntityStatus.ACTIVE)
				.expiredAt(DateUtils.dateToLocalDateTime(expiration))
				.token(token)
				.build();
	}

	private JwtAccessToken createJwtAccessToken(final String accessToken, final String refreshToken,
			final LocalDateTime accessTokenExpiration, final LocalDateTime refreshTokenExpiration ) {
		return JwtAccessToken.builder()
				.type("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accessTokenExpiration(DateUtils.localDateTimeToDate(accessTokenExpiration).getTime())
				.refreshTokenExpiration(DateUtils.localDateTimeToDate(refreshTokenExpiration).getTime())
				.build();
	}


	@Override
	public JwtAccessToken refreshToken(String jwtToken) {
		Claims claims = getClaims(jwtToken);
		if (!Objects.equals(claims.getIssuer(), issuer)) {
			throw new UnknownIssuerTokenException();
		}
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.REFRESH_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		if (!refreshTokenRepo.existsRefreshTokenByIdAndStatus(claims.getId())) {
			throw new RevokedJwtTokenException();
		}
		AccessToken accessToken = accessTokenRepo.findActiveAccessTokenById(claims.getSubject()).
				orElseThrow(RevokedJwtTokenException::new);
		accessToken.setStatus(CommonConstants.EntityStatus.DELETED);
		accessToken.getRefreshToken().setStatus(CommonConstants.EntityStatus.DELETED);
		accessTokenRepo.save(accessToken);
		User user = userService.findActiveUserById(accessToken.getUserId());
		setAuthenticationByRefreshToken(user, jwtToken);
		return createToken(true);
	}

	private Claims getClaims(String jwt) {
		try {
			return jwtParser.parseClaimsJws(jwt).getBody();
		}
		catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException |
			   PrematureJwtException | IllegalArgumentException e) {
			log.trace("Invalid JWT jwt", e);
			throw new InvalidJwtTokenException();
		}
	}

	private void setAuthenticationByRefreshToken(User user, String jwtToken) {
		setAuthenticationAfterSuccess(user, jwtToken);
	}

	private void setAuthenticationAfterSuccess(User user, String jwtToken) {
		UserDetails userDetails = buildPrincipalForRefreshTokenFromUser(user, jwtToken);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private UserDetails buildPrincipalForRefreshTokenFromUser(final User user, final String jwt) {
		DomainUserDetails userDetails = new DomainUserDetails(user, jwt);
		new AccountStatusUserDetailsChecker().check(userDetails);
		return userDetails;
	}

	@Override
	public void authorizeToken(String jwtToken) {
		Claims claims = getClaims(jwtToken);
		if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.ACCESS_TOKEN)) {
			throw new InvalidAudienceTokenException();
		}
		AccessToken accessToken = accessTokenRepo.findActiveAccessTokenById(claims.getId()).
				orElseThrow(RevokedJwtTokenException::new);
		User user = userService.findActiveUserById(accessToken.getUserId());
		setAuthenticationByAccessToken(user, claims.getSubject(), jwtToken);
	}

	private void setAuthenticationByAccessToken(User user, String preferredUsername, String jwtToken) {
		if (!Objects.equals(preferredUsername, user.getPreferredUsername())) {
			throw new InvalidJwtTokenException();
		}
		setAuthenticationAfterSuccess(user, jwtToken);
	}

	@Override
	public boolean isSelfIssuer(String jwtToken) {
		try {
			Base64.Decoder decoder = Base64.getUrlDecoder();
			var payload = decoder.decode(jwtToken.split("\\.")[1]);
			Map<String, String> claims = JACKSON_MAPPER.readValue(payload, Map.class);
			return Objects.equals(claims.get((Claims.ISSUER)), issuer);
		}
		catch (Exception e) {
			log.error("Cannot parse payload in jwtToken", e);
			throw new InvalidJwtTokenException();
		}
	}


	@Override
	public AbstractAuthenticationToken transferOauth2AuthenticationToUsernamePassswordAuthentication(Jwt jwt) {
		createUserIfNotExist(jwt);
		UserDetails userDetails = securityUserInfoProvider.getUserInfoByPrincipal(jwt.getClaimAsString("email"));
		return new UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
	}

	private void createUserIfNotExist(Jwt jwt) {
		final String email = jwt.getClaimAsString("email");
		if (userRepo.existsByEmail(email)) {
			return;
		}
		Role memberRole = roleRepo.findActiveRoleByName(CommonConstants.Role.DEFAULT_ROLE_MEMBER)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		User user = User.builder()
				.email(email)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username("openidc_" + jwt.getClaimAsString("preferred_username"))
				.preferredUsername(UUID.randomUUID().toString())
				.familyName(jwt.getClaimAsString("family_name"))
				.givenName(jwt.getClaimAsString("given_name"))
				.name(jwt.getClaimAsString("name"))
				.password(passwordEncoder.encode(PasswordGeneratorUtils.generateStrongPassword()))
				.roles(Set.of(memberRole))
				.enabled(CommonConstants.EntityStatus.ENABLED)
				.locked(CommonConstants.EntityStatus.UNLOCKED)
				.build();
		userRepo.save(user);
	}

	static {
		JACKSON_MAPPER = new ObjectMapper();
		JACKSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
