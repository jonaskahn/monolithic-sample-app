package io.github.tuyendev.mbs.common.entity.rdb;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = EntityName.USER)
public class User extends AbstractJdbcEntity<Long> {

	private String username;

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

	public User() {
	}

	public User(Long id, String username, String password, String email, Integer emailVerified, String familyName, String middleName, String givenName, String name, String unsigned_name, String phoneNumber, Integer phoneNumberVerified, Integer gender, LocalDate birthdate, Integer status, Set<UserRoleRef> roleRefs, Set<UserGroupRef> groupRefs) {
		this.id = id;
		this.username = username;
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
		this.roleRefs = roleRefs;
		this.groupRefs = groupRefs;
	}

	public static UserBuilder builder() {
		return new UserBuilder();
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

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
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
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(emailVerified, user.emailVerified) && Objects.equals(familyName, user.familyName) && Objects.equals(middleName, user.middleName) && Objects.equals(givenName, user.givenName) && Objects.equals(name, user.name) && Objects.equals(unsigned_name, user.unsigned_name) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(phoneNumberVerified, user.phoneNumberVerified) && Objects.equals(gender, user.gender) && Objects.equals(birthdate, user.birthdate) && Objects.equals(status, user.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, username, password, email, emailVerified, familyName, middleName, givenName, name, unsigned_name, phoneNumber, phoneNumberVerified, gender, birthdate, status);
	}

	public static class UserBuilder {
		private Long id;

		private String username;

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

		private Set<UserRoleRef> roleRefs;

		private Set<UserGroupRef> groupRefs;

		UserBuilder() {
		}

		public UserBuilder id(final Long id) {
			this.id = id;
			return this;
		}

		public UserBuilder username(final String username) {
			this.username = username;
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

		public UserBuilder roleRefs(final Set<UserRoleRef> roleRefs) {
			this.roleRefs = Objects.requireNonNullElse(roleRefs, new HashSet<>());
			return this;
		}

		public UserBuilder groupRefs(final Set<UserGroupRef> groupRefs) {
			this.groupRefs = Objects.requireNonNullElse(groupRefs, new HashSet<>());
			return this;
		}

		public User build() {
			return new User(this.id, this.username, this.password, this.email, this.emailVerified, this.familyName, this.middleName, this.givenName, this.name, this.unsigned_name, this.phoneNumber, this.phoneNumberVerified, this.gender, this.birthdate, this.status, this.roleRefs, this.groupRefs);
		}

		public String toString() {
			return "User.UserBuilder(id=" + this.id + ", username=" + this.username + ", password=" + "REMOVED" + ", email=" + this.email + ", emailVerified=" + this.emailVerified + ", familyName=" + this.familyName + ", middleName=" + this.middleName + ", givenName=" + this.givenName + ", name=" + this.name + ", unsigned_name=" + this.unsigned_name + ", phoneNumber=" + this.phoneNumber + ", phoneNumberVerified=" + this.phoneNumberVerified + ", gender=" + this.gender + ", birthdate=" + this.birthdate + ", status=" + this.status + ", roleRefs=" + this.roleRefs + ", groupRefs=" + this.groupRefs + ")";
		}
	}

}
