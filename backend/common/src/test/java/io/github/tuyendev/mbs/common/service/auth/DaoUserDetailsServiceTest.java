package io.github.tuyendev.mbs.common.service.auth;

import io.github.tuyendev.mbs.common.CommonConstants;
import io.github.tuyendev.mbs.common.entity.rdb.User;
import io.github.tuyendev.mbs.common.security.SecuredUserDetails;
import io.github.tuyendev.mbs.common.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaoUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DaoUserDetailsService daoUserDetailsService;

    @Test
    @DisplayName("Should return a secureduserdetails when the user is found")
    void loadUserByPreferredUsernameWhenUserIsFoundThenReturnSecuredUserDetails() {
        String preferredUsername = "test";
        User user =
                User.builder()
                        .id(1L)
                        .username("test")
                        .preferredUsername("test")
                        .password("$2a$10$X9Q/Zq3/8jxY.5zvQ4Z7A.6J0XbYmq/l9.5j8Kg1z7Vw4yf6hxWuG")
                        .email("test@gmail.com")
                        .emailVerified(CommonConstants.EntityStatus.VERIFIED)
                        .familyName("Test")
                        .middleName("Test")
                        .givenName("Test")
                        .name("Test Test Test")
                        .unsigned_name("Test Test Test")
                        .phoneNumber("+84123456789")
                        .phoneNumberVerified(CommonConstants.EntityStatus.VERIFIED)
                        .gender(0)
                        .birthdate(LocalDate.of(1990, 1, 1))
                        .enabled(CommonConstants.EntityStatus.ENABLED)
                        .locked(CommonConstants.EntityStatus.UNLOCKED)
                        .build();

        when(userService.findActiveUserByPreferredUsername(preferredUsername)).thenReturn(user);

        SecuredUserDetails securedUserDetails =
                daoUserDetailsService.loadUserByPreferredUsername(preferredUsername);

        assertNotNull(securedUserDetails);
        assertEquals(user, securedUserDetails.getUser());

        verify(userService, times(1)).findActiveUserByPreferredUsername(preferredUsername);
    }

    @Test
    @DisplayName("Should throw an exception when the user is not found")
    void loadUserByPreferredUsernameWhenUserIsNotFoundThenThrowException() {
        String preferredUsername = "test";
        Exception exception = new UsernameNotFoundException("username not found with preferredUsername test");
        when(daoUserDetailsService.loadUserByPreferredUsername(preferredUsername)).thenThrow(exception);
        try {
            userService.findActiveUserByPreferredUsername(preferredUsername);
            Assertions.fail();
        } catch (Exception e) {
            assertEquals(exception.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return a secureduserdetails when the user is found")
    void loadUserByUserIdWhenUserIsFoundThenReturnSecuredUserDetails() {
        Long userId = 1L;
        User user =
                User.builder()
                        .id(userId)
                        .username("username")
                        .preferredUsername("preferredUsername")
                        .password("password")
                        .email("email")
                        .emailVerified(CommonConstants.EntityStatus.VERIFIED)
                        .familyName("familyName")
                        .middleName("middleName")
                        .givenName("givenName")
                        .name("name")
                        .unsigned_name("unsigned_name")
                        .phoneNumber("phoneNumber")
                        .phoneNumberVerified(CommonConstants.EntityStatus.VERIFIED)
                        .gender(0)
                        .birthdate(LocalDate.now())
                        .enabled(CommonConstants.EntityStatus.ENABLED)
                        .locked(CommonConstants.EntityStatus.UNLOCKED)
                        .build();

        when(userService.findActiveUserById(userId)).thenReturn(user);

        SecuredUserDetails securedUserDetails = daoUserDetailsService.loadUserByUserId(userId);

        assertNotNull(securedUserDetails);
        assertEquals(user, securedUserDetails.getUser());
        assertEquals(userId.toString(), securedUserDetails.getUsername());

        verify(userService, times(1)).findActiveUserById(userId);
    }

    @Test
    @DisplayName("Should throw an exception when the user is not found")
    void loadUserByUserIdWhenUserIsNotFoundThenThrowException() {
        Long userId = 1L;
        Exception exception = new UsernameNotFoundException("username not found with id 1");
        when(daoUserDetailsService.loadUserByUserId(userId)).thenThrow(exception);
        try {
            userService.findActiveUserById(userId);
            Assertions.fail();
        } catch (Exception e) {
            assertEquals(exception.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return a user when the principal is an email")
    void loadUserByUsernameWhenPrincipalIsEmail() {
        String principal = "test@test.com";
        User user =
                User.builder()
                        .username("test")
                        .preferredUsername("test")
                        .password("$2a$10$X9Q/Zq3/8jxY7zQ4/0J1AuX5Z6lhY.vq7jw8p9xzfW5K.y6b4n2Xe")
                        .email("test@test.com")
                        .emailVerified(CommonConstants.EntityStatus.VERIFIED)
                        .familyName("Test")
                        .middleName("Test")
                        .givenName("Test")
                        .name("Test Test Test")
                        .unsigned_name("Test Test Test")
                        .phoneNumber("+84123456789")
                        .phoneNumberVerified(CommonConstants.EntityStatus.VERIFIED)
                        .gender(0)
                        .birthdate(LocalDate.of(2000, 1, 1))
                        .enabled(CommonConstants.EntityStatus.ENABLED)
                        .locked(CommonConstants.EntityStatus.UNLOCKED)
                        .build();

        when(userService.findUserByEmail(principal)).thenReturn(user);

        SecuredUserDetails securedUserDetails = daoUserDetailsService.loadUserByUsername(principal);

        assertNotNull(securedUserDetails);
        assertEquals(user, securedUserDetails.getUser());

        verify(userService, times(1)).findUserByEmail(principal);
    }

    @Test
    @DisplayName("Should return a user when the principal is a username")
    void loadUserByUsernameWhenPrincipalIsUsername() {
        String username = "username";
        User user =
                User.builder()
                        .username(username)
                        .preferredUsername(username)
                        .password("password")
                        .email("email@email.com")
                        .emailVerified(CommonConstants.EntityStatus.VERIFIED)
                        .familyName("familyName")
                        .middleName("middleName")
                        .givenName("givenName")
                        .name("name")
                        .unsigned_name("unsigned_name")
                        .phoneNumber("phoneNumber")
                        .phoneNumberVerified(CommonConstants.EntityStatus.VERIFIED)
                        .gender(0)
                        .birthdate(LocalDate.now())
                        .enabled(CommonConstants.EntityStatus.ENABLED)
                        .locked(CommonConstants.EntityStatus.UNLOCKED)
                        .build();

        when(userService.findUserByUsername(username)).thenReturn(user);

        SecuredUserDetails securedUserDetails = daoUserDetailsService.loadUserByUsername(username);

        assertNotNull(securedUserDetails);
        assertEquals(user, securedUserDetails.getUser());

        verify(userService, times(1)).findUserByUsername(username);
    }
}