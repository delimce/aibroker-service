package com.delimce.aibroker.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    private TestableBaseService baseService;
    private User testUser;
    private Authentication authentication;
    private SecurityContext securityContext;

    // Create a concrete implementation of BaseService for testing
    private static class TestableBaseService extends BaseService {
        // This class exists solely to test the abstract BaseService
        public User getAuthenticatedUser() {
            return fetchAuthenticatedUser();
        }
    }

    @BeforeEach
    void setUp() {
        baseService = new TestableBaseService();
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        testUser = User.builder()
                .id(1L)
                .name("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void fetchAuthenticatedUser_WithValidAuthentication_ShouldReturnUser() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when
            User result = baseService.getAuthenticatedUser();

            // then
            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getName(), result.getName());
            assertEquals(testUser.getLastName(), result.getLastName());
            assertEquals(testUser.getEmail(), result.getEmail());
            assertEquals(testUser.getStatus(), result.getStatus());
        }
    }

    @Test
    void fetchAuthenticatedUser_WithNullAuthentication_ShouldThrowIllegalArgumentException() {
        // given
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> baseService.getAuthenticatedUser()
            );

            assertTrue(exception.getMessage().contains("Failed to get User from authentication"));
        }
    }

    @Test
    void fetchAuthenticatedUser_WithNonUserPrincipal_ShouldThrowIllegalArgumentException() {
        // given
        String nonUserPrincipal = "not-a-user-object";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(nonUserPrincipal);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> baseService.getAuthenticatedUser()
            );

            assertTrue(exception.getMessage().contains("Failed to get User from authentication"));
        }
    }


    @Test
    void fetchAuthenticatedUser_WithExceptionDuringCast_ShouldThrowIllegalArgumentException() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenThrow(new RuntimeException("Authentication error"));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> baseService.getAuthenticatedUser()
            );

            assertTrue(exception.getMessage().contains("Failed to get User from authentication"));
            assertTrue(exception.getMessage().contains("Authentication error"));
        }
    }

    @Test
    void fetchAuthenticatedUser_WithDifferentUserStatuses_ShouldReturnUserRegardlessOfStatus() {
        // Test with different user statuses
        UserStatus[] statuses = {UserStatus.ACTIVE, UserStatus.INACTIVE, UserStatus.PENDING};

        for (UserStatus status : statuses) {
            // given
            User userWithStatus = User.builder()
                    .id(1L)
                    .name("Test")
                    .lastName("User")
                    .email("test@example.com")
                    .password("password")
                    .status(status)
                    .build();

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userWithStatus);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // when
                User result = baseService.getAuthenticatedUser();

                // then
                assertNotNull(result);
                assertEquals(status, result.getStatus());
            }
        }
    }
}