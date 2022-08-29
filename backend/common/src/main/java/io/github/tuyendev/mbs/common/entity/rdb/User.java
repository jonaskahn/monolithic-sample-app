package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.CommonConstants.EntityName;
import io.github.tuyendev.mbs.common.security.SecuredUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Table(value = EntityName.USER)
@Builder
public class User extends AbstractJdbcEntity<Long> implements SecuredUser {

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

    private Integer enabled;

    private Integer locked;

    @Transient
    private Set<Role> roles = new HashSet<>();

    @Transient
    private Set<Group> groups = new HashSet<>();

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
                Integer gender, LocalDate birthdate, Integer enabled, Integer locked, Set<Role> roles,
                Set<Group> groups, Set<UserRoleRef> roleRefs, Set<UserGroupRef> groupRefs, Set<String> authorities) {
        this.username = username;
        this.preferredUsername = preferredUsername;
        this.password = password;
        this.email = email;
        this.emailVerified = Objects.requireNonNullElse(emailVerified, CommonConstants.EntityStatus.UNVERIFIED);
        this.familyName = familyName;
        this.middleName = middleName;
        this.givenName = givenName;
        this.name = name;
        this.unsigned_name = unsigned_name;
        this.phoneNumber = phoneNumber;
        this.phoneNumberVerified = Objects.requireNonNullElse(phoneNumberVerified, CommonConstants.EntityStatus.UNVERIFIED);
        this.gender = gender;
        this.birthdate = birthdate;
        this.enabled = Objects.requireNonNullElse(enabled, CommonConstants.EntityStatus.DISABLED);
        this.locked = Objects.requireNonNullElse(locked, CommonConstants.EntityStatus.LOCKED);
        this.roles = Objects.requireNonNullElse(roles, new HashSet<>());
        this.groups = Objects.requireNonNullElse(groups, new HashSet<>());
        this.roleRefs = Objects.requireNonNullElse(roleRefs, fromRoles(roles));
        this.groupRefs = Objects.requireNonNullElse(groupRefs, fromGroups(groups));
        this.authorities = Objects.requireNonNullElse(authorities, new HashSet<>());
    }

    private void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    private Set<UserRoleRef> fromRoles(Collection<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) return new HashSet<>();
        return StreamEx.of(roles)
                .map(Role::getId)
                .map(UserRoleRef::new)
                .toSet();
    }

    private Set<UserGroupRef> fromGroups(Collection<Group> groups) {
        if (CollectionUtils.isEmpty(groups)) return new HashSet<>();
        return StreamEx.of(groups)
                .map(Group::getId)
                .map(UserGroupRef::new)
                .toSet();
    }

    public Set<Long> roleIds() {
        if (CollectionUtils.isEmpty(roleRefs)) return new HashSet<>();
        return StreamEx.of(roleRefs)
                .map(UserRoleRef::getRoleId)
                .toImmutableSet();
    }

    public Set<Long> groupIds() {
        if (CollectionUtils.isEmpty(groupRefs)) return new HashSet<>();
        return StreamEx.of(groupRefs)
                .map(UserGroupRef::getGroupId)
                .toImmutableSet();
    }

    public Set<String> getAuthorityNames() {
        if (CollectionUtils.isEmpty(roles)) return new HashSet<>();
        return StreamEx.of(roles)
                .map(Role::getAuthorities)
                .flatMap(Collection::stream)
                .map(Authority::getName)
                .toImmutableSet();
    }
}
