package io.github.tuyendev.mbs.common.service.role;

import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.repository.rdb.AuthorityRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepo;

	private final AuthorityRepository authorityRepo;

	@Override
	public Role findActiveByName(String name) {
		if (Objects.equals(name, CommonConstants.Role.DEFAULT_ROLE_ADMIN)) {
			return findAdminRole();
		}
		Role role = roleRepo.findActiveRoleByName(name)
				.orElseThrow(RoleNotFoundException::new);
		Set<Authority> authorities = authorityRepo.findAllActiveByIdIn(role.authorityIds());
		role.setAuthorities(authorities);
		return role;
	}

	@Override
	public Set<Role> findAllActiveByIds(Set<Long> ids) {
		Role admin = findAdminRole();
		if (ids.contains(admin.getId())) {
			return Set.of(admin);
		}
		Set<Role> roles = roleRepo.findAllActiveByIdIn(ids);
		roles.forEach(role -> {
			Set<Authority> authorities = authorityRepo.findAllActiveByIdIn(role.authorityIds());
			role.setAuthorities(authorities);
		});
		return roles;
	}

	@Override
	public Role findAdminRole() {
		Role admin = roleRepo.findActiveRoleByName(CommonConstants.Role.DEFAULT_ROLE_ADMIN)
				.orElseThrow(RoleNotFoundException::new);
		Set<Authority> authorities = authorityRepo.findAllActive();
		admin.setAuthorities(authorities);
		return admin;
	}

}
