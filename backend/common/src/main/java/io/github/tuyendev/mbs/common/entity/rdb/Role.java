package io.github.tuyendev.mbs.common.entity.rdb;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import one.util.streamex.StreamEx;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = EntityName.ROLE)
public class Role extends AbstractJdbcEntity<Long> {

	private Long parentId;

	@Transient
	private Role parent;

	private String name;

	private String description;

	private Integer status;

	@MappedCollection(idColumn = "role_id")
	private Set<RoleUserRef> userRefs = new HashSet<>();

	@MappedCollection(idColumn = "role_id")
	private Set<Authority> authorities = new HashSet<>();

	public Role() {
	}

	public Role(Long id, Long parentId, String name, String description, Integer status, Set<RoleUserRef> userRefs, Set<Authority> authorities) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.description = description;
		this.status = status;
		this.userRefs = userRefs;
		this.authorities = authorities;
	}

	public Set<Long> userIds() {
		return StreamEx.of(userRefs).map(RoleUserRef::getUserId).toImmutableSet();
	}

	public Set<Long> authorityIds() {
		return StreamEx.of(authorities).map(Authority::getId).toImmutableSet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Role role = (Role) o;
		return Objects.equals(id, role.id) && Objects.equals(parentId, role.parentId) && Objects.equals(name, role.name) && Objects.equals(description, role.description) && Objects.equals(status, role.status) && Objects.equals(userRefs, role.userRefs) && Objects.equals(authorities, role.authorities);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name, description, status, userRefs, authorities);
	}

	public static class RoleBuilder {
		private Long id;

		private Long parentId;

		private String name;

		private String description;

		private Integer status;

		private Set<RoleUserRef> userRefs;

		private Set<Authority> authorities;

		RoleBuilder() {
		}

		public RoleBuilder id(final Long id) {
			this.id = id;
			return this;
		}

		public RoleBuilder parentId(final Long parentId) {
			this.parentId = parentId;
			return this;
		}

		public RoleBuilder name(final String name) {
			this.name = name;
			return this;
		}

		public RoleBuilder description(final String description) {
			this.description = description;
			return this;
		}

		public RoleBuilder status(final Integer status) {
			this.status = status;
			return this;
		}

		public RoleBuilder userRefs(final Set<RoleUserRef> userRefs) {
			this.userRefs = Objects.requireNonNullElse(userRefs, new HashSet<>());
			return this;
		}

		public RoleBuilder authorities(final Set<Authority> authorities) {
			this.authorities = Objects.requireNonNullElse(authorities, new HashSet<>());
			return this;
		}

		public Role build() {
			return new Role(this.id, this.parentId, this.name, this.description, this.status, this.userRefs, this.authorities);
		}

		public String toString() {
			return "Role.RoleBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", status=" + this.status + ", userRefs=" + this.userRefs + ", authorities=" + this.authorities + ")";
		}
	}
}
