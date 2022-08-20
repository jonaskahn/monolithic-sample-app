package io.github.tuyendev.mbs.common.service.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;

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
			List<String> roleHierarchies = this.roleRepo.findAllActive().stream()
					.map(this::buildAuthorityHierarchy)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			((RoleHierarchyImpl) roleHierarchy).setHierarchy(String.join("\n", roleHierarchies));
		}
	}

	private String buildAuthorityHierarchy(Role role) {
		Role parentRole = role.getParent();
		if (Objects.isNull(parentRole)) {
			return null;
		}
		Map<String, String> parentAuthorities = authoritiesByPrefix(parentRole.getAuthorities());
		Map<String, String> authorities = authoritiesByPrefix(role.getAuthorities());
		List<String> authorityHierarchies = new ArrayList<>();
		for (Map.Entry<String, String> entry : parentAuthorities.entrySet()) {
			final String prefix = entry.getKey();
			final String parent = entry.getValue();
			String child = authorities.get(prefix);
			if (Objects.nonNull(child)) {
				authorityHierarchies.add(parent + " > " + child);
			}
		}
		return String.join("\n", authorityHierarchies);
	}

	private Map<String, String> authoritiesByPrefix(Set<Authority> authorities) {
		Set<String> strAuthorities = StreamEx.of(authorities)
				.map(Authority::getName)
				.toImmutableSet();
		Map<String, String> result = new HashMap<>();
		for (String authority : strAuthorities) {
			if (authority.contains(CommonConstants.Privilege.READ_PREFIX)) {
				result.put(CommonConstants.Privilege.READ_PREFIX, authority);
				continue;
			}
			if (authority.contains(CommonConstants.Privilege.UPDATE_PREFIX)) {
				result.put(CommonConstants.Privilege.UPDATE_PREFIX, authority);
				continue;
			}
			if (authority.contains(CommonConstants.Privilege.WRITE_PREFIX)) {
				result.put(CommonConstants.Privilege.WRITE_PREFIX, authority);
				continue;
			}
			if (authority.contains(CommonConstants.Privilege.DELETE_PREFIX)) {
				result.put(CommonConstants.Privilege.DELETE_PREFIX, authority);
			}
		}
		return result;
	}
}
