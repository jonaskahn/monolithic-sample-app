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

	private String name;

	private String description;

	private Integer status;

	@Transient
	private Set<Authority> authorities = new HashSet<>();

	@MappedCollection(idColumn = "role_id")
	private Set<RoleUserRef> userRefs = new HashSet<>();

	@MappedCollection(idColumn = "role_id")
	private Set<RoleAuthorityRef> authorityRefs = new HashSet<>();

	public Role() {
	}

	public Role(Long id, Long parentId, String name, String description, Integer status, Set<RoleUserRef> userRefs, Set<RoleAuthorityRef> authorityRefs) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.description = description;
		this.status = status;
		this.userRefs = userRefs;
		this.authorityRefs = authorityRefs;
	}


	public Set<Long> userIds() {
		return StreamEx.of(userRefs).map(RoleUserRef::getUserId).toImmutableSet();
	}

	public Set<Long> authorityIds() {
		return StreamEx.of(authorityRefs).map(RoleAuthorityRef::getAuthorityId).toImmutableSet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Role role = (Role) o;
		return Objects.equals(id, role.id) && Objects.equals(parentId, role.parentId) && Objects.equals(name, role.name) && Objects.equals(description, role.description) && Objects.equals(status, role.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, parentId, name, description, status);
	}

	public static class RoleBuilder {
		private Long id;

		private Long parentId;

		private String name;

		private String description;

		private Integer status;

		private Set<RoleUserRef> userRefs;

		private Set<RoleAuthorityRef> authorityRefs;

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

		public RoleBuilder authorityRefs(final Set<RoleAuthorityRef> authorityRefs) {
			this.authorityRefs = Objects.requireNonNullElse(authorityRefs, new HashSet<>());
			return this;
		}

		public Role build() {
			return new Role(this.id, this.parentId, this.name, this.description, this.status, this.userRefs, this.authorityRefs);
		}

		public String toString() {
			return "Role.RoleBuilder(id=" + this.id + ", parentId=" + this.parentId + ", name=" + this.name + ", description=" + this.description + ", status=" + this.status + ", userRefs=" + this.userRefs + ", authorityRefs=" + this.authorityRefs + ")";
		}
	}
}
