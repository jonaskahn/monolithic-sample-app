package io.github.tuyendev.mbs.common.configurer;

import java.util.List;
import java.util.Objects;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;

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

	private final PasswordEncoder passwordEncoder;

	private final String adminUsername;

	private final String adminEmail;

	private final String adminPassword;

	BootstrapAppConfigurer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = DEFAULT_ADMIN_USERNAME;
		this.adminEmail = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_EMAIL"), DEFAULT_ADMIN_EMAIL);
		this.adminPassword = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_PASSWORD"), DEFAULT_ADMIN_PASSWORD);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	void onReady() {
		createRoleAdminIfNotExist();
		createRoleMemberIfNotExist();
		createAdminUserIfNotExist();
	}

	void createRoleAdminIfNotExist() {
		createRoleIfNotExist(DEFAULT_ROLE_ADMIN, adminPrivileges);
	}

	private void createRoleIfNotExist(String roleName, List<String> rolePrivileges) {
		//TODO
	}

	void createRoleMemberIfNotExist() {
		createRoleIfNotExist(DEFAULT_ROLE_MEMBER, basisPrivileges);
	}

	void createAdminUserIfNotExist() {
		if (userRepo.existsActiveUserByUsername(this.adminUsername)) {
			return;
		}
		//Set<Role> adminRole = roleRepo.findAllActiveRoleByName(ROLE_ADMIN);
		User user = User.builder()
				.email(this.adminEmail)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username(this.adminUsername)
				.password(passwordEncoder.encode(this.adminPassword))
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
		userRepo.save(user);
	}
}
