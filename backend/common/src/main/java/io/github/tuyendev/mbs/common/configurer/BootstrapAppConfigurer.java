package io.github.tuyendev.mbs.common.configurer;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.annotation.context.FeaturePrivilegeClaim;
import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Feature;
import io.github.tuyendev.mbs.common.entity.rdb.Role;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.repository.rdb.AuthorityRepository;
import io.github.tuyendev.mbs.common.repository.rdb.FeatureRepository;
import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import one.util.streamex.StreamEx;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

import static io.github.tuyendev.mbs.common.CommonConstants.Privilege.adminPrivileges;
import static io.github.tuyendev.mbs.common.CommonConstants.Privilege.basisPrivileges;
import static io.github.tuyendev.mbs.common.CommonConstants.Role.DEFAULT_ROLE_ADMIN;
import static io.github.tuyendev.mbs.common.CommonConstants.Role.DEFAULT_ROLE_MEMBER;

@Configuration
class BootstrapAppConfigurer {

    private static final String DEFAULT_ADMIN_USERNAME = "root";

    private static final String DEFAULT_ADMIN_EMAIL = "admin@localhost";

    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

    private final FeatureRepository featureRepo;

    private final AuthorityRepository authorityRepo;

    private final RoleRepository roleRepo;

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    private final String adminUsername;

    private final String adminEmail;

    private final String adminPassword;

    private final List<FeaturePrivilegeClaim> privilegeClaims;

    BootstrapAppConfigurer(FeatureRepository featureRepo, AuthorityRepository authorityRepo, RoleRepository roleRepo, UserRepository userRepo,
                           PasswordEncoder passwordEncoder, List<FeaturePrivilegeClaim> privilegeClaims) {
        this.featureRepo = featureRepo;
        this.authorityRepo = authorityRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.privilegeClaims = privilegeClaims;
        this.adminUsername = DEFAULT_ADMIN_USERNAME;
        this.adminEmail = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_EMAIL"), DEFAULT_ADMIN_EMAIL);
        this.adminPassword = Objects.requireNonNullElse(System.getenv("DEFAULT_ADMIN_PASSWORD"), DEFAULT_ADMIN_PASSWORD);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(rollbackFor = Exception.class)
    public void onReady() {
        createAdminFeatureAndRoleIfNotExist();
        createMemberFeatureAndRoleIfNotExist();
        createAdminUserIfNotExist();
        updateFeaturePrivileges();
    }

    private void createAdminFeatureAndRoleIfNotExist() {
        createFeatureAndRoleIfNotExist(DEFAULT_ROLE_ADMIN, adminPrivileges);
    }

    private void createFeatureAndRoleIfNotExist(String name, List<String> rolePrivileges) {
        if (!featureRepo.existsByName(name)) {
            Set<Authority> authorities = StreamEx.of(rolePrivileges)
                    .map(privilege -> Authority.builder()
                            .name(privilege)
                            .description(privilege)
                            .status(CommonConstants.EntityStatus.ACTIVE)
                            .build())
                    .toImmutableSet();
            Feature feature = Feature.builder()
                    .name(name)
                    .authorities(authorities)
                    .description(name)
                    .type(CommonConstants.FeatureType.SYSTEM)
                    .build();
            featureRepo.saveOrUpdate(feature);
        }
        if (!roleRepo.existsByName(name)) {
            Feature feature = featureRepo.findFeatureByName(name)
                    .orElseThrow(() -> new RuntimeException("This should never happen"));
            Role role = Role.builder()
                    .name(name)
                    .description(name)
                    .authorities(feature.getAuthorities())
                    .status(CommonConstants.EntityStatus.ACTIVE)
                    .build();
            roleRepo.save(role);
        }
    }

    private void createMemberFeatureAndRoleIfNotExist() {
        createFeatureAndRoleIfNotExist(DEFAULT_ROLE_MEMBER, basisPrivileges);
    }

    private void createAdminUserIfNotExist() {
        if (userRepo.existsByUsername(this.adminUsername)) {
            return;
        }
        Role adminRole = roleRepo.findActiveRoleByName(DEFAULT_ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("This should never happen"));
        User user = User.builder()
                .email(this.adminEmail)
                .emailVerified(CommonConstants.EntityStatus.VERIFIED)
                .username(this.adminUsername)
                .preferredUsername(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(this.adminPassword))
                .roles(Set.of(adminRole))
                .enabled(CommonConstants.EntityStatus.ENABLED)
                .locked(CommonConstants.EntityStatus.UNLOCKED)
                .build();
        userRepo.save(user);
    }

    private void updateFeaturePrivileges() {
        privilegeClaims.forEach(this::createOrUpdateFeaturePrivilege);
    }

    private void createOrUpdateFeaturePrivilege(FeaturePrivilegeClaim claim) {
        final String name = claim.getName();
        if (featureRepo.existsByName(name)) {
            updateFeature(claim);
        } else createFeature(claim);
    }

    private void createFeature(FeaturePrivilegeClaim claim) {
        Set<Authority> authorities = new HashSet<>();
        claim.getPrivileges().forEach((name, desc) -> {
            authorities.add(Authority.builder()
                    .name(name)
                    .description(desc)
                    .status(CommonConstants.EntityStatus.ACTIVE)
                    .build()
            );
        });
        Feature feature = Feature.builder()
                .name(claim.getName())
                .description(claim.getDescription())
                .type(CommonConstants.FeatureType.APP)
                .authorities(authorities)
                .build();
        featureRepo.saveOrUpdate(feature);
    }

    private void updateFeature(FeaturePrivilegeClaim claim) {
        Feature feature = featureRepo.findFeatureByName(claim.getName())
                .orElseThrow(() -> new RuntimeException("This should never happen"));
        Map<String, Authority> existedAuthorities = StreamEx.of(authorityRepo.findAllByFeatureId(feature.getId()))
                .toMap(Authority::getName, Function.identity());
        existedAuthorities.forEach((name, auth) -> auth.setStatus(CommonConstants.EntityStatus.INACTIVE));
        Map<String, String> authorities = claim.getPrivileges();
        Set<Authority> newAuthorities = new HashSet<>();

        for (Map.Entry<String, String> entry : authorities.entrySet()) {
            final String name = entry.getKey();
            final String desc = entry.getValue();
            Authority authority = existedAuthorities.get(name);
            if (Objects.nonNull(authority)) {
                authority.setStatus(CommonConstants.EntityStatus.ACTIVE);
                authority.setDescription(desc);
                authority.setStatus(CommonConstants.EntityStatus.ACTIVE);
                existedAuthorities.remove(name);
            } else {
                authority = Authority.builder()
                        .featureId(feature.getId())
                        .name(name)
                        .description(desc)
                        .status(CommonConstants.EntityStatus.ACTIVE)
                        .build();
            }
            newAuthorities.add(authority);
        }
        newAuthorities.addAll(existedAuthorities.values());
        feature.setDescription(claim.getDescription());
        feature.setAuthorities(newAuthorities);
        featureRepo.saveOrUpdate(feature);
    }
}
