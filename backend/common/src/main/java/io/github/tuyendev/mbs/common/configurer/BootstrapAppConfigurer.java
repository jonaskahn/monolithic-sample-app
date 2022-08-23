package io.github.tuyendev.mbs.common.configurer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Feature;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.FeatureRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import one.util.streamex.StreamEx;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static io.github.tuyendev.mbs.common.CommonConstants.Privilege.adminPrivileges;
import static io.github.tuyendev.mbs.common.CommonConstants.Privilege.basisPrivileges;
import static io.github.tuyendev.mbs.common.CommonConstants.Role.DEFAULT_ROLE_ADMIN;
import static io.github.tuyendev.mbs.common.CommonConstants.Role.DEFAULT_ROLE_MEMBER;

@Configuration
class BootstrapAppConfigurer {

	private static final String DEFAULT_ADMIN_USERNAME = "root";

	private static final String DEFAULT_ADMIN_EMAIL = "admin@localhost";

	private static final String DEFAULT_ADMIN_PASSWORD = "admin";


	private final FeatureRepository featureRepo;

	private final RoleRepository roleRepo;

	private final UserRepository userRepo;

	private final PasswordEncoder passwordEncoder;

	private final String adminUsername;

	private final String adminEmail;

	private final String adminPassword;

	BootstrapAppConfigurer(FeatureRepository featureRepo, RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.featureRepo = featureRepo;
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = DEFAULT_ADMIN_USERNAME;
		this.adminEmail = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_EMAIL"), DEFAULT_ADMIN_EMAIL);
		this.adminPassword = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_PASSWORD"), DEFAULT_ADMIN_PASSWORD);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional(rollbackFor = Exception.class)
	public void onReady() {
		createAdminFeatureAndRoleIfNotExist();
		createMemberFeatureAndRoleIfNotExist();
		createAdminUserIfNotExist();
	}

	private void createAdminFeatureAndRoleIfNotExist() {
		createFeatureAndRoleIfNotExist(DEFAULT_ROLE_ADMIN, adminPrivileges);
	}

	private void createFeatureAndRoleIfNotExist(String name, List<String> rolePrivileges) {
		if (!featureRepo.existsByName(name)) {
			Set<Authority> authorities = StreamEx.of(rolePrivileges)
					.map(privilege -> Authority.builder()
							.name(privilege)
							.description(privilege)
							.status(CommonConstants.EntityStatus.ACTIVE)
							.build())
					.toImmutableSet();
			Feature feature = Feature.builder()
					.name(name)
					.authorities(authorities)
					.description(name)
					.type(CommonConstants.FeatureType.SYSTEM)
					.build();
			featureRepo.saveOrUpdate(feature);
		}
		if (!roleRepo.existsByName(name)) {
			Feature feature = featureRepo.findFeatureByName(name)
					.orElseThrow(() -> new RuntimeException("This should never happen"));
			Role role = Role.builder()
					.name(name)
					.description(name)
					.authorities(feature.getAuthorities())
					.status(CommonConstants.EntityStatus.ACTIVE)
					.build();
			roleRepo.create(role);
		}
	}

	private void createMemberFeatureAndRoleIfNotExist() {
		createFeatureAndRoleIfNotExist(DEFAULT_ROLE_MEMBER, basisPrivileges);
	}

	private void createAdminUserIfNotExist() {
		if (userRepo.existsByUsername(this.adminUsername)) {
			return;
		}
		Role adminRole = roleRepo.findActiveRoleByName(DEFAULT_ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		User user = User.builder()
				.email(this.adminEmail)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username(this.adminUsername)
				.preferredUsername(UUID.randomUUID().toString())
				.password(passwordEncoder.encode(this.adminPassword))
				.roles(Set.of(adminRole))
				.enabled(CommonConstants.EntityStatus.ENABLED)
				.locked(CommonConstants.EntityStatus.UNLOCKED)
				.build();
		userRepo.save(user);
	}
}
