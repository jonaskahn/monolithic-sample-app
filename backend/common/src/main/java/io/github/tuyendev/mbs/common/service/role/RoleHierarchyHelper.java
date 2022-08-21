package io.github.tuyendev.mbs.common.service.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import one.util.streamex.StreamEx;

import static io.github.tuyendev.mbs.common.CommonConstants.Privilege;

abstract class RoleHierarchyHelper {

	public static List<String> buildAuthorities(Collection<Role> roles) {
		Map<String, List<String>> privilegesByPrefix = buildRolePrivileges(roles);
		List<String> result = new ArrayList<>();
		privilegesByPrefix.values().forEach(ac -> {
			result.add(String.join("\n", ac));
		});
		return result;
	}

	private static Map<String, List<String>> buildRolePrivileges(Collection<Role> roles) {
		List<RoleDto> roleDto = transferRolesToDto(roles);
		Map<String, Set<String>> comprehensivePrivileges = Map.of(
				Privilege.READ_PREFIX, new HashSet<>(),
				Privilege.WRITE_PREFIX, new HashSet<>(),
				Privilege.UPDATE_PREFIX, new HashSet<>(),
				Privilege.DELETE_PREFIX, new HashSet<>()
		);

		for (RoleDto role : roleDto) {
			Map<String, List<String>> potentialPrivileges = Map.of(
					Privilege.READ_PREFIX, new ArrayList<>(),
					Privilege.WRITE_PREFIX, new ArrayList<>(),
					Privilege.UPDATE_PREFIX, new ArrayList<>(),
					Privilege.DELETE_PREFIX, new ArrayList<>()
			);

			markUpHierarchyPrivileges(role, potentialPrivileges);
			Map<String, String> simplifiedPrivileges = simplifiedPrivileges(potentialPrivileges);
			comprehensivePrivileges.get(Privilege.READ_PREFIX).add(simplifiedPrivileges.get(Privilege.READ_PREFIX));
			comprehensivePrivileges.get(Privilege.WRITE_PREFIX).add(simplifiedPrivileges.get(Privilege.WRITE_PREFIX));
			comprehensivePrivileges.get(Privilege.UPDATE_PREFIX).add(simplifiedPrivileges.get(Privilege.UPDATE_PREFIX));
			comprehensivePrivileges.get(Privilege.DELETE_PREFIX).add(simplifiedPrivileges.get(Privilege.DELETE_PREFIX));
		}
		return Map.of(
				Privilege.READ_PREFIX, removeUnnecessaryHierarchyPrivileges(comprehensivePrivileges.get(Privilege.READ_PREFIX)),
				Privilege.WRITE_PREFIX, removeUnnecessaryHierarchyPrivileges(comprehensivePrivileges.get(Privilege.WRITE_PREFIX)),
				Privilege.UPDATE_PREFIX, removeUnnecessaryHierarchyPrivileges(comprehensivePrivileges.get(Privilege.UPDATE_PREFIX)),
				Privilege.DELETE_PREFIX, removeUnnecessaryHierarchyPrivileges(comprehensivePrivileges.get(Privilege.DELETE_PREFIX))
		);
	}

	private static Map<String, String> simplifiedPrivileges(Map<String, List<String>> potentialPrivileges) {
		Collections.reverse(potentialPrivileges.get(Privilege.READ_PREFIX));
		Collections.reverse(potentialPrivileges.get(Privilege.WRITE_PREFIX));
		Collections.reverse(potentialPrivileges.get(Privilege.UPDATE_PREFIX));
		Collections.reverse(potentialPrivileges.get(Privilege.DELETE_PREFIX));
		return Map.of(
				Privilege.READ_PREFIX, String.join(" > ", potentialPrivileges.get(Privilege.READ_PREFIX)),
				Privilege.WRITE_PREFIX, String.join(" > ", potentialPrivileges.get(Privilege.WRITE_PREFIX)),
				Privilege.UPDATE_PREFIX, String.join(" > ", potentialPrivileges.get(Privilege.UPDATE_PREFIX)),
				Privilege.DELETE_PREFIX, String.join(" > ", potentialPrivileges.get(Privilege.DELETE_PREFIX))
		);
	}

	private static List<RoleDto> transferRolesToDto(Collection<Role> roles) {
		List<RoleDto> result = new ArrayList<>();
		for (Role role : roles) {
			RoleDto roleDto = buildRoleDto(role);
			result.add(roleDto);
		}
		Map<Long, RoleDto> roleDtoMap = StreamEx.of(result).toMap(RoleDto::getId, Function.identity());
		for (RoleDto roleDto : result) {
			RoleDto parent = roleDtoMap.get(roleDto.getParentId());
			roleDto.setParent(parent);
		}
		return result;
	}

	private static List<String> removeUnnecessaryHierarchyPrivileges(Set<String> privileges) {
		List<String> clonedPrivileges = new ArrayList<>(privileges);
		List<String> result = new ArrayList<>(privileges);
		for (String check : privileges) {
			for (String candidate : clonedPrivileges) {
				if (!Objects.equals(candidate, check) && check.contains(candidate)) {
					result.remove(candidate);
				}
			}
		}
		return result;
	}

	private static void markUpHierarchyPrivileges(RoleDto role, Map<String, List<String>> privileges) {
		if (Objects.nonNull(role.getReadAuthority())) {
			List<String> readPrivileges = privileges.get(Privilege.READ_PREFIX);
			readPrivileges.add(role.getReadAuthority());
		}
		if (Objects.nonNull(role.getWriteAuthority())) {
			List<String> writePrivileges = privileges.get(Privilege.WRITE_PREFIX);
			writePrivileges.add(role.getWriteAuthority());
		}
		if (Objects.nonNull(role.getUpdateAuthority())) {
			List<String> updatePrivileges = privileges.get(Privilege.UPDATE_PREFIX);
			updatePrivileges.add(role.getUpdateAuthority());
		}
		if (Objects.nonNull(role.getDeleteAuthority())) {
			List<String> deletePrivileges = privileges.get(Privilege.DELETE_PREFIX);
			deletePrivileges.add(role.getDeleteAuthority());
		}
		if (role.getParent() == null) {
			return;
		}
		markUpHierarchyPrivileges(role.getParent(), privileges);
	}

	private static RoleDto buildRoleDto(Role role) {
		if (role == null) {
			return null;
		}
		Map<String, String> authorities = authoritiesByPrefix(role.getAuthorities());
		return RoleDto.builder()
				.id(role.getId())
				.parentId(role.getParentId())
				.name(role.getName())
				.authorities(authorities)
				.build();
	}

	private static Map<String, String> authoritiesByPrefix(Set<Authority> authorities) {
		Set<String> strAuthorities = StreamEx.of(authorities)
				.map(Authority::getName)
				.toImmutableSet();
		Map<String, String> result = new HashMap<>();
		for (String authority : strAuthorities) {
			if (authority.contains(Privilege.READ_PREFIX)) {
				result.put(Privilege.READ_PREFIX, authority);
				continue;
			}
			if (authority.contains(Privilege.UPDATE_PREFIX)) {
				result.put(Privilege.UPDATE_PREFIX, authority);
				continue;
			}
			if (authority.contains(Privilege.WRITE_PREFIX)) {
				result.put(Privilege.WRITE_PREFIX, authority);
				continue;
			}
			if (authority.contains(Privilege.DELETE_PREFIX)) {
				result.put(Privilege.DELETE_PREFIX, authority);
			}
		}
		return result;
	}
}
