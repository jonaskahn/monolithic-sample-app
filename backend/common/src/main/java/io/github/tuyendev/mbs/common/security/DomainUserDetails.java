package io.github.tuyendev.mbs.common.security;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;

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
		return Optional.of(user)
				.map(User::getRoles).stream()
				.flatMap(Collection::stream)
				.map(Role::getAuthorities)
				.flatMap(Collection::stream)
				.map(authority -> new SimpleGrantedAuthority(authority.getName()))
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return Objects.requireNonNullElse(principal, user.getId().toString());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !Objects.equals(user.getStatus(), CommonConstants.EntityStatus.LOCK);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Objects.equals(user.getStatus(), CommonConstants.EntityStatus.ENABLED);
	}

	public User getUser() {
		return user;
	}
}
