package com.delimce.aibroker.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

class AccountVerifiedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenInterface jwtTokenInterface;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AccountVerifiedService accountVerifiedService;

    private static final String TEST_TOKEN = "test-token";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldActivateUserSuccessfully() throws JwtTokenException {
        // Arrange
        User pendingUser = User.builder()
                .email(TEST_EMAIL)
                .status(UserStatus.PENDING)
                .build();

        User activatedUser = User.builder()
                .email(TEST_EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        UserMinDetail expectedResponse = new UserMinDetail(TEST_EMAIL, activatedUser.getCreatedAt());

        when(jwtTokenInterface.extractEmail(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(pendingUser);
        when(jwtTokenInterface.isTokenValid(TEST_TOKEN, pendingUser)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(activatedUser);
        when(userMapper.userToUserMinDetail(activatedUser)).thenReturn(expectedResponse);

        // Act
        UserMinDetail response = accountVerifiedService.execute(TEST_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotChangeStatusForActiveUser() throws JwtTokenException {
        // Arrange
        User activeUser = User.builder()
                .email(TEST_EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        UserMinDetail expectedResponse = new UserMinDetail(TEST_EMAIL, activeUser.getCreatedAt());

        when(jwtTokenInterface.extractEmail(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(activeUser);
        when(jwtTokenInterface.isTokenValid(TEST_TOKEN, activeUser)).thenReturn(true);
        when(userMapper.userToUserMinDetail(activeUser)).thenReturn(expectedResponse);

        // Act
        UserMinDetail response = accountVerifiedService.execute(TEST_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionForInvalidToken() throws JwtTokenException {
        // Arrange
        when(jwtTokenInterface.extractEmail(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            accountVerifiedService.execute(TEST_TOKEN);
        });
    }

    @Test
    void shouldThrowExceptionForExpiredToken() throws JwtTokenException {
        // Arrange
        User user = User.builder()
                .email(TEST_EMAIL)
                .status(UserStatus.PENDING)
                .build();

        when(jwtTokenInterface.extractEmail(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(user);
        when(jwtTokenInterface.isTokenValid(TEST_TOKEN, user)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            accountVerifiedService.execute(TEST_TOKEN);
        });
    }
}