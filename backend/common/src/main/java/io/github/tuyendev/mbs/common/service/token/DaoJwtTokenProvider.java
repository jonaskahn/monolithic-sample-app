package io.github.tuyendev.mbs.common.service.token;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.AccessToken;
import io.github.tuyendev.mbs.common.entity.rdb.RefreshToken;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.AccessTokenRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RefreshTokenRepository;
import io.github.tuyendev.mbs.common.security.SecuredUser;
import io.github.tuyendev.mbs.common.security.SecuredUserDetails;
import io.github.tuyendev.mbs.common.security.jwt.JwtAccessToken;
import io.github.tuyendev.mbs.common.security.jwt.JwtTokenProvider;
import io.github.tuyendev.mbs.common.service.user.UserService;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;
import io.github.tuyendev.mbs.common.utils.DateUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DaoJwtTokenProvider implements JwtTokenProvider {

    private static final ObjectMapper JACKSON_MAPPER;

    static {
        JACKSON_MAPPER = new ObjectMapper();
        JACKSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final UserDetailsChecker postCheckUserStatus = new AccountStatusUserDetailsChecker();
    private final JwtParser jwtParser;
    private final Key secretKey;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final AccessTokenRepository accessTokenRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    @Value("${app.common.jwt.issuer}")
    private String issuer;
    @Value("${app.common.jwt.access-token-expiration-in-minutes}")
    private long accessTokenExpirationInMinutes;
    @Value("${app.common.jwt.refresh-token-expiration-in-minutes}")
    private long refreshTokenExpirationInMinutes;
    @Value("${app.common.jwt.remember-me-expiration-in-minutes}")
    private long rememberMeExpirationInMinutes;


    public DaoJwtTokenProvider(@Value("${app.common.jwt.secret-key}") String jwtSecretKey,
                               AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
                               AccessTokenRepository accessTokenRepo, RefreshTokenRepository refreshTokenRepo) {
        this.userService = userService;
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
        SecuredUser currentUser = AppContextUtils.getCurrentLoginUser()
                .orElseThrow(() -> new RuntimeException("This should never happen since the authentication is completed before."));
        final Date issuedAt = new Date();
        AccessToken accessToken = createAccessToken(currentUser, issuedAt, rememberMe);
        RefreshToken refreshToken = createRefreshToken(accessToken, issuedAt, rememberMe);
        accessToken.setRefreshToken(refreshToken);
        accessTokenRepo.save(accessToken);
        return createJwtAccessToken(accessToken.getToken(), refreshToken.getToken(),
                accessToken.getExpiredAt(), refreshToken.getExpiredAt());
    }

    private AccessToken createAccessToken(final SecuredUser user, Date issuedAt, boolean rememberMe) {
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
                .claim("aut", user.getAuthorityNames())
                .signWith(secretKey)
                .compact();
        return AccessToken.builder()
                .id(id)
                .userId(user.getId())
                .token(token)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .status(CommonConstants.EntityStatus.ACTIVE)
                .newEntity()
                .build();
    }

    private Date getExpirationDate(Date issuedAt, long defaultExpiration, boolean rememberMe) {
        return new Date(issuedAt.getTime() + (rememberMe ? (defaultExpiration + rememberMeExpirationInMinutes) : defaultExpiration) * 1000 * 60);
    }

    private RefreshToken createRefreshToken(AccessToken accessToken, Date issuedAt, boolean rememberMe) {
        Date expiration = getExpirationDate(issuedAt, refreshTokenExpirationInMinutes, rememberMe);
        final String id = UUID.randomUUID().toString();
        final String token = Jwts.builder()
                .setIssuer(issuer)
                .setId(id)
                .setAudience(CommonConstants.TokenAudience.REFRESH_TOKEN)
                .setSubject(accessToken.getId())
                .setIssuedAt(issuedAt)
                .setNotBefore(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
        return RefreshToken.builder()
                .id(id)
                .userId(accessToken.getUserId())
                .status(CommonConstants.EntityStatus.ACTIVE)
                .expiredAt(DateUtils.dateToLocalDateTime(expiration))
                .token(token)
                .newEntity()
                .build();
    }

    private JwtAccessToken createJwtAccessToken(final String accessToken, final String refreshToken,
                                                final LocalDateTime accessTokenExpiration, final LocalDateTime refreshTokenExpiration) {
        return JwtAccessToken.builder()
                .type("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accExpiredAt(DateUtils.localDateTimeToDate(accessTokenExpiration).getTime())
                .refExpiredAt(DateUtils.localDateTimeToDate(refreshTokenExpiration).getTime())
                .build();
    }

    @Override
    public JwtAccessToken renewToken(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        if (!Objects.equals(claims.getIssuer(), issuer)) {
            throw new UnknownIssuerTokenException();
        }
        if (!Objects.equals(claims.getAudience(), CommonConstants.TokenAudience.REFRESH_TOKEN)) {
            throw new InvalidAudienceTokenException();
        }
        RefreshToken refreshToken = refreshTokenRepo.findActiveRefreshTokenBy(claims.getId())
                .orElseThrow(RevokedJwtTokenException::new);
        if (!Objects.equals(refreshToken.getAccessTokenId(), claims.getSubject())) {
            throw new InvalidJwtTokenException();
        }
        accessTokenRepo.inactiveAccessToken(refreshToken.getAccessTokenId());
        User user = userService.findActiveUserById(refreshToken.getUserId());
        setAuthenticationByRefreshToken(user, jwtToken);
        return createToken(isPreviousRefreshTokenRememberMe(claims));
    }

    private boolean isPreviousRefreshTokenRememberMe(Claims claims) {
        Long realExpiration = Math.abs(claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
        return realExpiration.equals((refreshTokenExpirationInMinutes + rememberMeExpirationInMinutes) * 1000 * 60);
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
        SecuredUserDetails userDetails = new SecuredUserDetails(user, jwt);
        postCheckUserStatus.check(userDetails);
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
        } catch (Exception e) {
            log.error("Cannot parse payload in jwtToken", e);
            throw new InvalidJwtTokenException();
        }
    }

    @Override
    public void revokeMe() {
        Long userId = AppContextUtils.getCurrentLoginUserId();
        Set<AccessToken> accessTokens = accessTokenRepo.findAllActiveByUserId(userId);
        accessTokens.forEach(accessToken -> {
            accessToken.setStatus(CommonConstants.EntityStatus.INACTIVE);
            accessToken.getRefreshToken().setStatus(CommonConstants.EntityStatus.INACTIVE);
        });
        accessTokenRepo.saveAll(accessTokens);
    }
}
