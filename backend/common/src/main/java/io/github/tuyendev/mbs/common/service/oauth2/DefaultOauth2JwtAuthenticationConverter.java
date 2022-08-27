package io.github.tuyendev.mbs.common.service.oauth2;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import io.github.tuyendev.mbs.common.security.oauth2.Oauth2JwtAuthenticationConverter;
import io.github.tuyendev.mbs.common.utils.PasswordGeneratorUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class DefaultOauth2JwtAuthenticationConverter implements Oauth2JwtAuthenticationConverter {

    private final UserDetailsChecker postCheckUserStatus = new AccountStatusUserDetailsChecker();

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder;

    public DefaultOauth2JwtAuthenticationConverter(UserDetailsService userDetailsService, UserRepository userRepo,
                                                   RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        parseJwtToInternalUser(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getClaimAsString("email"));
        postCheckUserStatus.check(userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void parseJwtToInternalUser(Jwt jwt) {
        final String email = jwt.getClaimAsString("email");
        if (userRepo.existsByEmail(email)) {
            updateInternalUser(jwt);
        } else {
            createInternalUser(jwt);
        }
    }

    private void createInternalUser(Jwt jwt) {
        Role memberRole = roleRepo.findActiveRoleByName(CommonConstants.Role.DEFAULT_ROLE_MEMBER)
                .orElseThrow(() -> new RuntimeException("This should never happen"));
        User user = User.builder()
                .email(jwt.getClaimAsString("email"))
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

    // Todo update user
    private void updateInternalUser(Jwt jwt) {

    }
}
