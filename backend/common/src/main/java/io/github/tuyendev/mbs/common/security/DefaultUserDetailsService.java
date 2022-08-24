package io.github.tuyendev.mbs.common.security;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class DefaultUserDetailsService implements UserDetailsService {

	private final SecurityUserInfoProvider securityUserInfoProvider;

	public DefaultUserDetailsService(SecurityUserInfoProvider securityUserInfoProvider) {
		this.securityUserInfoProvider = securityUserInfoProvider;
	}

	@Override
	public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
		log.debug("authenticating {}", principal);
		return securityUserInfoProvider.getUserInfoByPrincipal(principal.toLowerCase(Locale.ENGLISH));
	}
}
