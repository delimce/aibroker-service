package com.delimce.aibroker.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AccountRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenInterface jwtTokenInterface;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AccountRegisterService accountRegisterService;

    private UserRegistrationRequest request;
    private User user;
    private UserCreatedResponse response;

    @BeforeEach
    void setUp() {
        request = new UserRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setPasswordConfirmation("password123");

        user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        response = new UserCreatedResponse(
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getTempToken(),
                user.getCreatedAt()
                );
    }

    @Test
    void shouldCreateUserSuccessfully() throws UserAlreadyExistsException {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserCreatedResponse(user)).thenReturn(response);

        UserCreatedResponse result = accountRegisterService.execute(request);

        assertNotNull(result);
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userMapper).userToUserCreatedResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDontMatch() {
        request.setPasswordConfirmation("differentPassword");

        assertThrows(IllegalArgumentException.class, () -> {
            accountRegisterService.execute(request);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(user);

        assertThrows(UserAlreadyExistsException.class, () -> {
            accountRegisterService.execute(request);
        });

        verify(userRepository).findByEmail(request.getEmail());
    }
}