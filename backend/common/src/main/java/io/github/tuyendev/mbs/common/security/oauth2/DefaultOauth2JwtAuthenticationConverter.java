package io.github.tuyendev.mbs.common.security.oauth2;

import java.util.Set;
import java.util.UUID;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import io.github.tuyendev.mbs.common.utils.PasswordGeneratorUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class DefaultOauth2JwtAuthenticationConverter implements Oauth2JwtAuthenticationConverter {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public AbstractAuthenticationToken convert(Jwt source) {
		final String email = source.getClaimAsString("email");
		createUserIfNotExist(source);

		return null;
	}

	private void createUserIfNotExist(Jwt source) {
		final String email = source.getClaimAsString("email");
		if (userRepo.existsActiveUserByEmail(email)) {
			return;
		}
		Role memberRole = roleRepo.findActiveRoleByName(CommonConstants.Role.DEFAULT_ROLE_MEMBER)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		User user = User.builder()
				.email(email)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username("openidc_" + source.getClaimAsString("preferred_username"))
				.preferredUsername(UUID.randomUUID().toString())
				.familyName(source.getClaimAsString("family_name"))
				.givenName(source.getClaimAsString("given_name"))
				.name(source.getClaimAsString("name"))
				.password(passwordEncoder.encode(PasswordGeneratorUtils.generateStrongPassword()))
				.roles(Set.of(memberRole))
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
		userRepo.save(user);
	}
}
