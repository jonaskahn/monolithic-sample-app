package io.github.tuyendev.mbs.common.service.auth;

import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.security.DomainUserDetailsService;
import io.github.tuyendev.mbs.common.security.SecuredUserDetails;
import io.github.tuyendev.mbs.common.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class DaoUserDetailsService implements DomainUserDetailsService {

    private static final EmailValidator emailValidator = new EmailValidator();

    private final UserService userService;

    public DaoUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SecuredUserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        log.debug("authenticating {}", principal);
        final String lowercasePrincipal = principal.toLowerCase(Locale.ENGLISH);
        User user = emailValidator.isValid(principal, null) ? userService.findUserByEmail(lowercasePrincipal)
                : userService.findUserByUsername(lowercasePrincipal);
        return new SecuredUserDetails(user, lowercasePrincipal);
    }

    @Override
    public SecuredUserDetails loadUserByUserId(Long userId) {
        User user = userService.findActiveUserById(userId);
        return new SecuredUserDetails(user, userId.toString());
    }

    @Override
    public SecuredUserDetails loadUserByPreferredUsername(String preferredUsername) {
        User user = userService.findActiveUserByPreferredUsername(preferredUsername);
        return new SecuredUserDetails(user, preferredUsername);
    }
}
