package io.github.tuyendev.mbs.common.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface DomainUserDetailsService extends UserDetailsService {

	SecuredUserDetails loadUserByUserId(final Long userId);

	SecuredUserDetails loadUserByPreferredUsername(final String preferredUsername);
}
