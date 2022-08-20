package io.github.tuyendev.mbs.common.configurer;

import java.util.List;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
class BootstrapAppConfigurer {

	//TODO configurable
	public static final String DEFAULT_ADMIN_USERNAME = "admin";

	public static final String DEFAULT_ADMIN_EMAIL = "admin@localhost";

	public static final String DEFAULT_ADMIN_PASSWORD = "admin";

	public static final String ROLE_MEMBER = "MEMBER";

	public static final String ROLE_ADMIN = "ADMIN";

	public static final List<String> basisPrivileges = List.of("READ", "WRITE", "UPDATE", "DELETE");

	public static final List<String> adminPrivileges = List.of("SUPREME_READ", "SUPREME_WRITE", "SUPREME_UPDATE", "SUPREME_DELETE");

	private final RoleRepository roleRepo;

	private final UserRepository userRepo;

	private final PasswordEncoder passwordEncoder;

	BootstrapAppConfigurer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	void onReady() {
		createRoleAdminIfNotExist();
		createRoleMemberIfNotExist();
		createAdminUserIfNotExist();
	}

	void createRoleAdminIfNotExist() {
		createRoleIfNotExist(ROLE_ADMIN, adminPrivileges);
	}

	private void createRoleIfNotExist(String roleName, List<String> rolePrivileges) {
		//TODO
	}

	void createRoleMemberIfNotExist() {
		createRoleIfNotExist(ROLE_MEMBER, basisPrivileges);
	}

	void createAdminUserIfNotExist() {
		if (userRepo.existsActiveUserByUsername(DEFAULT_ADMIN_USERNAME)) {
			return;
		}
		//Set<Role> adminRole = roleRepo.findAllActiveRoleByName(ROLE_ADMIN);
		User user = User.builder()
				.email(DEFAULT_ADMIN_EMAIL)
				.emailVerified(CommonConstants.EntityStatus.VERIFIED)
				.username(DEFAULT_ADMIN_USERNAME)
				.password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
				.status(CommonConstants.EntityStatus.ACTIVE)
				.build();
		userRepo.save(user);
	}
}
