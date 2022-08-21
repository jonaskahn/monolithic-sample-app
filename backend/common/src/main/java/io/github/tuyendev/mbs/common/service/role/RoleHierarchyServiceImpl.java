package io.github.tuyendev.mbs.common.service.role;

import java.util.List;

import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

	private final RoleHierarchy roleHierarchy;

	private final RoleRepository roleRepo;

	private final Object lock = new Object();

	@Override
	public void reload() {
		synchronized (lock) {
			List<String> authorities = RoleHierarchyHelper.buildAuthorities(this.roleRepo.findAllActive());
			((RoleHierarchyImpl) roleHierarchy).setHierarchy(String.join("\n", authorities));
		}
	}
}
