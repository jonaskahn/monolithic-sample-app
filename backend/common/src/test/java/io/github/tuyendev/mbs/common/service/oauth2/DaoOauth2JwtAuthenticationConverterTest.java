package io.github.tuyendev.mbs.common.service.oauth2;

import io.github.tuyendev.mbs.common.repository.rdb.RoleRepository;
import io.github.tuyendev.mbs.common.repository.rdb.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DaoOauth2JwtAuthenticationConverterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DaoOauth2JwtAuthenticationConverter daoOauth2JwtAuthenticationConverter;

    @Test
    @DisplayName("Should throw an exception when the user is not found")
    void convertWhenUserIsNotFoundThenThrowException() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("email")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenThrow(new UsernameNotFoundException(""));

        assertThrows(
                UsernameNotFoundException.class,
                () -> daoOauth2JwtAuthenticationConverter.convert(jwt));
    }

    @Test
    @DisplayName("Should throw an exception when the user is disabled")
    void convertWhenUserIsDisabledThenThrowException() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("email")).thenReturn("test@test.com");
        when(userRepo.existsByEmail(anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenThrow(new DisabledException("User is disabled"));

        assertThrows(
                DisabledException.class, () -> daoOauth2JwtAuthenticationConverter.convert(jwt));
    }
}