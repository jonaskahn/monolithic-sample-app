package io.github.tuyendev.mbs.common.security;

public interface SecurityUserInfoProvider {
	DomainUserDetails getUserInfoByPrincipal(String principal);
}
