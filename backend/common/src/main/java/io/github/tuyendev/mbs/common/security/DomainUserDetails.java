package io.github.tuyendev.mbs.common.security;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import one.util.streamex.StreamEx;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DomainUserDetails implements UserDetails {

	private final User user;

	private final String principal;

	public DomainUserDetails(@NonNull User user, @NonNull String principal) {
		this.user = user;
		this.principal = principal;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return StreamEx.of(user.getAuthorityNames())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return principal;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return Objects.equals(this.user.getLocked(), CommonConstants.EntityStatus.UNLOCKED);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Objects.equals(user.getEnabled(), CommonConstants.EntityStatus.ENABLED);
	}

	public User getUser() {
		return user;
	}
}
