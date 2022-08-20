package io.github.tuyendev.mbs.common.entity.rdb;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import one.util.streamex.StreamEx;

import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = EntityName.AUTHORITY)
public class Authority extends AbstractJdbcEntity<Long> {

	private String name;

	private String description;

	private Integer status;

	@MappedCollection(idColumn = "authority_id")
	private Set<AuthorityRoleRef> roleRefs = new HashSet<>();

	public Authority() {
	}

	public Authority(Long id, String name, String description, Integer status, Set<AuthorityRoleRef> roleRefs) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
		this.roleRefs = roleRefs;
	}

	public Set<Long> getRoleIds() {
		return StreamEx.of(roleRefs).map(AuthorityRoleRef::getRoleId).toImmutableSet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Authority authority = (Authority) o;
		return Objects.equals(id, authority.id) && Objects.equals(name, authority.name) && Objects.equals(description, authority.description) && Objects.equals(status, authority.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, name, description, status);
	}

	public static class AuthorityBuilder {
		private Long id;

		private String name;

		private String description;

		private Integer status;

		private Set<AuthorityRoleRef> roleRefs;

		AuthorityBuilder() {
		}

		public AuthorityBuilder id(final Long id) {
			this.id = id;
			return this;
		}

		public AuthorityBuilder name(final String name) {
			this.name = name;
			return this;
		}

		public AuthorityBuilder description(final String description) {
			this.description = description;
			return this;
		}

		public AuthorityBuilder status(final Integer status) {
			this.status = status;
			return this;
		}

		public AuthorityBuilder roleRefs(final Set<AuthorityRoleRef> roleRefs) {
			this.roleRefs = Objects.requireNonNullElse(roleRefs, new HashSet<>());
			return this;
		}

		public Authority build() {
			return new Authority(this.id, this.name, this.description, this.status, this.roleRefs);
		}

		public String toString() {
			return "Authority.AuthorityBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", status=" + this.status + ", roleRefs=" + this.roleRefs + ")";
		}
	}
}
