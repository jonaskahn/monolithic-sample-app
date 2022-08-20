package io.github.tuyendev.mbs.common.configurer;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import io.github.tuyendev.mbs.common.service.role.RoleHierarchyService;
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

	//TODO configurable
	private static final String DEFAULT_ADMIN_USERNAME = "root";

	private static final String DEFAULT_ADMIN_EMAIL = "admin@localhost";

	private static final String DEFAULT_ADMIN_PASSWORD = "admin";

	private final RoleRepository roleRepo;

	private final UserRepository userRepo;

	private final RoleHierarchyService roleHierarchyService;

	private final PasswordEncoder passwordEncoder;

	private final String adminUsername;

	private final String adminEmail;

	private final String adminPassword;

	BootstrapAppConfigurer(RoleRepository roleRepo, UserRepository userRepo, RoleHierarchyService roleHierarchyService, PasswordEncoder passwordEncoder) {
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
		this.roleHierarchyService = roleHierarchyService;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = DEFAULT_ADMIN_USERNAME;
		this.adminEmail = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_EMAIL"), DEFAULT_ADMIN_EMAIL);
		this.adminPassword = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_PASSWORD"), DEFAULT_ADMIN_PASSWORD);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional(rollbackFor = Exception.class)
	public void onReady() {
		createRoleAdminIfNotExist();
		createRoleMemberIfNotExist();
		createRoleRelation();
		createAdminUserIfNotExist();
		reloadRoleHierarchy();
	}

	private void reloadRoleHierarchy() {
		roleHierarchyService.reload();
	}


	private void createRoleAdminIfNotExist() {
		createRoleIfNotExist(DEFAULT_ROLE_ADMIN, adminPrivileges);
	}

	private void createRoleIfNotExist(String roleName, List<String> rolePrivileges) {
		if (roleRepo.existsByName(roleName)) {
			return;
		}
		Set<Authority> authorities = StreamEx.of(rolePrivileges)
				.map(privilege -> Authority.builder()
						.name(privilege)
						.description(privilege)
						.status(CommonConstants.EntityStatus.ACTIVE)
						.build())
				.toImmutableSet();
		Role role = Role.builder()
				.name(roleName)
				.description(roleName)
				.authorities(authorities)
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
		roleRepo.save(role);
	}

	private void createRoleMemberIfNotExist() {
		createRoleIfNotExist(DEFAULT_ROLE_MEMBER, basisPrivileges);
	}

	private void createRoleRelation() {
		if (roleRepo.existsByName(DEFAULT_ROLE_ADMIN) && roleRepo.existsByName(DEFAULT_ROLE_MEMBER)) {
			return;
		}
		Role admin = roleRepo.findActiveRoleByName(DEFAULT_ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		Role member = roleRepo.findActiveRoleByName(DEFAULT_ROLE_MEMBER)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		member.setParentId(admin.getId());
		roleRepo.save(member);
	}

	private void createAdminUserIfNotExist() {
		if (userRepo.existsActiveUserByUsername(this.adminUsername)) {
			return;
		}
		Role adminRole = roleRepo.findActiveRoleByName(DEFAULT_ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("This should never happen"));
		User user = User.builder()
				.email(this.adminEmail)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username(this.adminUsername)
				.password(passwordEncoder.encode(this.adminPassword))
				.role(Set.of(adminRole))
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
		userRepo.save(user);
	}
}
