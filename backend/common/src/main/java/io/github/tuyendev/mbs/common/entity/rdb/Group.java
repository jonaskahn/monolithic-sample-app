package io.github.tuyendev.mbs.common.entity.rdb;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import one.util.streamex.StreamEx;

import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Builder
@Table(value = EntityName.GROUP)
public class Group extends AbstractJdbcEntity<Long> {

	private String name;

	private String domain;

	private String email;

	private String description;

	private String status;

	@MappedCollection(idColumn = "group_id")
	private Set<GroupUserRef> userRefs = new HashSet<>();

	public Group() {
	}

	public Group(Long id, String name, String domain, String email, String description, String status, Set<GroupUserRef> userRefs) {
		this.id = id;
		this.name = name;
		this.domain = domain;
		this.email = email;
		this.description = description;
		this.status = status;
		this.userRefs = userRefs;
	}

	private Set<Long> getUserIds() {
		return StreamEx.of(userRefs).map(GroupUserRef::getUserId).toImmutableSet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Group group = (Group) o;
		return Objects.equals(id, group.id) && Objects.equals(name, group.name) && Objects.equals(domain, group.domain) && Objects.equals(email, group.email) && Objects.equals(description, group.description) && Objects.equals(status, group.status) && Objects.equals(userRefs, group.userRefs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, name, domain, email, description, status, userRefs);
	}

	public static class GroupBuilder {
		private Long id;

		private String name;

		private String domain;

		private String email;

		private String description;

		private String status;

		private Set<GroupUserRef> userRefs;

		GroupBuilder() {
		}

		public GroupBuilder id(final Long id) {
			this.id = id;
			return this;
		}

		public GroupBuilder name(final String name) {
			this.name = name;
			return this;
		}

		public GroupBuilder domain(final String domain) {
			this.domain = domain;
			return this;
		}

		public GroupBuilder email(final String email) {
			this.email = email;
			return this;
		}

		public GroupBuilder description(final String description) {
			this.description = description;
			return this;
		}

		public GroupBuilder status(final String status) {
			this.status = status;
			return this;
		}

		public GroupBuilder userRefs(final Set<GroupUserRef> userRefs) {
			this.userRefs = Objects.requireNonNullElse(userRefs, new HashSet<>());
			return this;
		}

		public Group build() {
			return new Group(this.id, this.name, this.domain, this.email, this.description, this.status, this.userRefs);
		}

		public String toString() {
			return "Group.GroupBuilder(id=" + this.id + ", name=" + this.name + ", domain=" + this.domain + ", email=" + this.email + ", description=" + this.description + ", status=" + this.status + ", userRefs=" + this.userRefs + ")";
		}
	}
}
