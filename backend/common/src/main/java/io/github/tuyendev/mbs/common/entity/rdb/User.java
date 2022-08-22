package io.github.tuyendev.mbs.common.entity.rdb;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = EntityName.USER)
@Builder
public class User extends AbstractJdbcEntity<Long> {

	private String username;

	private String preferredUsername;

	private String password;

	private String email;

	private Integer emailVerified;

	private String familyName;

	private String middleName;

	private String givenName;

	private String name;

	private String unsigned_name;

	private String phoneNumber;

	private Integer phoneNumberVerified;

	private Integer gender;

	private LocalDate birthdate;

	private Integer status;

	@Transient
	private Set<Role> roles = new HashSet<>();

	@MappedCollection(idColumn = "user_id")
	private Set<UserRoleRef> roleRefs = new HashSet<>();

	@MappedCollection(idColumn = "user_id")
	private Set<UserGroupRef> groupRefs = new HashSet<>();

	@Transient
	private Set<String> authorities = new HashSet<>();

	public User() {
	}

	public User(String username, String preferredUsername, String password, String email,
			Integer emailVerified, String familyName, String middleName, String givenName,
			String name, String unsigned_name, String phoneNumber, Integer phoneNumberVerified,
			Integer gender, LocalDate birthdate, Integer status, Set<Role> roles,
			Set<UserRoleRef> roleRefs, Set<UserGroupRef> groupRefs, Set<String> authorities) {
		this.username = username;
		this.preferredUsername = preferredUsername;
		this.password = password;
		this.email = email;
		this.emailVerified = emailVerified;
		this.familyName = familyName;
		this.middleName = middleName;
		this.givenName = givenName;
		this.name = name;
		this.unsigned_name = unsigned_name;
		this.phoneNumber = phoneNumber;
		this.phoneNumberVerified = phoneNumberVerified;
		this.gender = gender;
		this.birthdate = birthdate;
		this.status = status;
		this.roles = roles;
		this.roleRefs = roleRefs;
		this.groupRefs = groupRefs;
		this.authorities = authorities;
	}

	public void addRole(Role role) {
		roleRefs.add(new UserRoleRef(role.getId()));
	}

	public void addRoles(Collection<Role> roles) {
		roles.forEach(this::addRole);
	}

	public Set<Long> roleIds() {
		return StreamEx.of(roleRefs).map(UserRoleRef::getRoleId).toImmutableSet();
	}

	public Set<Long> getGroupIds() {
		return StreamEx.of(groupRefs).map(UserGroupRef::getGroupId).toImmutableSet();
	}

	public Set<String> getAuthorities() {
		return Optional.of(this)
				.map(User::getRoles).stream()
				.flatMap(Collection::stream)
				.map(Role::getAuthorities)
				.flatMap(Collection::stream)
				.map(Authority::getName)
				.collect(Collectors.toSet());
	}


	@Override
	public String toString() {
		return "User{" +
				"username='" + username + '\'' +
				", preferredUsername='" + preferredUsername + '\'' +
				", password='" + "REMOVED" + '\'' +
				", email='" + email + '\'' +
				", emailVerified=" + emailVerified +
				", familyName='" + familyName + '\'' +
				", middleName='" + middleName + '\'' +
				", givenName='" + givenName + '\'' +
				", name='" + name + '\'' +
				", unsigned_name='" + unsigned_name + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", phoneNumberVerified=" + phoneNumberVerified +
				", gender=" + gender +
				", birthdate=" + birthdate +
				", status=" + status +
				", roles=" + roles +
				", roleRefs=" + roleRefs +
				", groupRefs=" + groupRefs +
				", authorities=" + authorities +
				'}';
	}

	public static class UserBuilder {
		private String username;

		private String preferredUsername;

		private String password;

		private String email;

		private Integer emailVerified;

		private String familyName;

		private String middleName;

		private String givenName;

		private String name;

		private String unsigned_name;

		private String phoneNumber;

		private Integer phoneNumberVerified;

		private Integer gender;

		private LocalDate birthdate;

		private Integer status;

		private Set<Role> roles;

		private Set<UserRoleRef> roleRefs;

		private Set<UserGroupRef> groupRefs;

		private Set<String> authorities;

		UserBuilder() {
		}

		public UserBuilder username(final String username) {
			this.username = username;
			return this;
		}

		public UserBuilder preferredUsername(final String preferredUsername) {
			this.preferredUsername = preferredUsername;
			return this;
		}

		public UserBuilder password(final String password) {
			this.password = password;
			return this;
		}

		public UserBuilder email(final String email) {
			this.email = email;
			return this;
		}

		public UserBuilder emailVerified(final Integer emailVerified) {
			this.emailVerified = emailVerified;
			return this;
		}

		public UserBuilder familyName(final String familyName) {
			this.familyName = familyName;
			return this;
		}

		public UserBuilder middleName(final String middleName) {
			this.middleName = middleName;
			return this;
		}

		public UserBuilder givenName(final String givenName) {
			this.givenName = givenName;
			return this;
		}

		public UserBuilder name(final String name) {
			this.name = name;
			return this;
		}

		public UserBuilder unsigned_name(final String unsigned_name) {
			this.unsigned_name = unsigned_name;
			return this;
		}

		public UserBuilder phoneNumber(final String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public UserBuilder phoneNumberVerified(final Integer phoneNumberVerified) {
			this.phoneNumberVerified = phoneNumberVerified;
			return this;
		}

		public UserBuilder gender(final Integer gender) {
			this.gender = gender;
			return this;
		}

		public UserBuilder birthdate(final LocalDate birthdate) {
			this.birthdate = birthdate;
			return this;
		}

		public UserBuilder status(final Integer status) {
			this.status = status;
			return this;
		}

		public UserBuilder roles(final Set<Role> roles) {
			this.roles = roles;
			this.roleRefs = StreamEx.of(roles)
					.map(Role::getId)
					.map(UserRoleRef::new)
					.toImmutableSet();
			return this;
		}

		public UserBuilder roleRefs(final Set<UserRoleRef> roleRefs) {
			this.roleRefs = Objects.requireNonNullElse(roleRefs, new HashSet<>());
			return this;
		}

		public UserBuilder groupRefs(final Set<UserGroupRef> groupRefs) {
			this.groupRefs = Objects.requireNonNullElse(groupRefs, new HashSet<>());
			return this;
		}

		public User build() {
			return new User(this.username, this.preferredUsername, this.password, this.email, this.emailVerified, this.familyName, this.middleName, this.givenName, this.name, this.unsigned_name, this.phoneNumber, this.phoneNumberVerified, this.gender, this.birthdate, this.status, this.roles, this.roleRefs, this.groupRefs, this.authorities);
		}

		public String toString() {
			return "User.UserBuilder(username=" + this.username + ", preferredUsername=" + this.preferredUsername + ", password=" + this.password + ", email=" + this.email + ", emailVerified=" + this.emailVerified + ", familyName=" + this.familyName + ", middleName=" + this.middleName + ", givenName=" + this.givenName + ", name=" + this.name + ", unsigned_name=" + this.unsigned_name + ", phoneNumber=" + this.phoneNumber + ", phoneNumberVerified=" + this.phoneNumberVerified + ", gender=" + this.gender + ", birthdate=" + this.birthdate + ", status=" + this.status + ", roles=" + this.roles + ", roleRefs=" + this.roleRefs + ", groupRefs=" + this.groupRefs + ", authorities=" + this.authorities + ")";
		}
	}
}
