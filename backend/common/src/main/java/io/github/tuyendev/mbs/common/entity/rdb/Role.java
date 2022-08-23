package io.github.tuyendev.mbs.common.entity.rdb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@Builder

@Table(value = EntityName.ROLE)
public class Role extends AbstractJdbcEntity<Long> {

	protected Long id;

	private Long parentId;

	private String name;

	private String description;

	private Integer status;

	@Transient
	private Set<Authority> authorities;

	@MappedCollection(idColumn = "role_id")
	private Set<RoleUserRef> userRefs;

	@MappedCollection(idColumn = "role_id")
	private Set<RoleAuthorityRef> authorityRefs;

	public Role() {
	}

	public Role(Long id, Long parentId, String name, String description, Integer status, Set<Authority> authorities, Set<RoleUserRef> userRefs, Set<RoleAuthorityRef> authorityRefs) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.description = description;
		this.status = status;
		this.authorities = authorities;
		this.userRefs = Objects.requireNonNullElse(userRefs, new HashSet<>());
		this.authorityRefs = Objects.requireNonNullElse(authorityRefs, fromAuthorities(authorities));
	}

	private Set<RoleAuthorityRef> fromAuthorities(Set<Authority> authorities) {
		if (CollectionUtils.isEmpty(authorities)) return new HashSet<>();
		return StreamEx.of(authorities)
				.filter(Objects::nonNull)
				.map(Authority::getId)
				.map(RoleAuthorityRef::new)
				.toSet();
	}

	public Collection<Long> authorityIds() {
		if (CollectionUtils.isEmpty(authorityRefs)) return Set.of();
		return StreamEx.of(authorityRefs)
				.map(RoleAuthorityRef::getAuthorityId)
				.toImmutableSet();
	}
}
