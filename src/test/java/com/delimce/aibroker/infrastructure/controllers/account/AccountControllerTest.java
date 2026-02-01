package com.delimce.aibroker.infrastructure.controllers.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delimce.aibroker.application.account.AccountLoginService;
import com.delimce.aibroker.application.account.AccountRegisterService;
import com.delimce.aibroker.application.account.AccountVerifiedService;
import com.delimce.aibroker.domain.dto.requests.users.UserLoginRequest;
import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.exceptions.account.UserIsNotActiveException;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.DecodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    private static final String EMAIL = "john.doe@example.com";
    private static final DateTimeFormatter ISO_FORMATTER =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountLoginService accountLoginService;

    @MockBean
    private AccountRegisterService accountRegisterService;

    @MockBean
    private AccountVerifiedService accountVerifiedService;

    @MockBean
    private JwtTokenInterface jwtTokenInterface;

    @MockBean
    private UserRepository userRepository;

    @Test
    void login_withValidCredentials_returnsOkResponse() throws Throwable {
        UserLoginRequest request = UserLoginRequest.builder()
            .email(EMAIL)
            .password("strongPass1")
            .build();

        UserLoggedResponse serviceResponse = new UserLoggedResponse(
            "jwt-token",
            "John",
            "Doe",
            EMAIL
        );

        when(
            accountLoginService.execute(any(UserLoginRequest.class))
        ).thenReturn(serviceResponse);

        mockMvc
            .perform(
                post("/account/auth")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info.token").value("jwt-token"))
            .andExpect(jsonPath("$.info.email").value(EMAIL));

        verify(accountLoginService).execute(any(UserLoginRequest.class));
    }

    @Test
    void login_whenUserIsNotActive_returnsUnauthorized() throws Throwable {
        UserLoginRequest request = UserLoginRequest.builder()
            .email(EMAIL)
            .password("strongPass1")
            .build();

        when(
            accountLoginService.execute(any(UserLoginRequest.class))
        ).thenThrow(new UserIsNotActiveException());

        mockMvc
            .perform(
                post("/account/auth")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("User is not active"));

        verify(accountLoginService).execute(any(UserLoginRequest.class));
    }

    @Test
    void login_whenSecurityValidationFails_returnsUnauthorized()
        throws Throwable {
        UserLoginRequest request = UserLoginRequest.builder()
            .email(EMAIL)
            .password("strongPass1")
            .build();

        when(
            accountLoginService.execute(any(UserLoginRequest.class))
        ).thenThrow(new SecurityValidationException("Invalid token", null) {});

        mockMvc
            .perform(
                post("/account/auth")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Invalid token"));

        verify(accountLoginService).execute(any(UserLoginRequest.class));
    }

    @Test
    void login_whenServiceThrowsIllegalArgument_returnsBadRequest()
        throws Throwable {
        UserLoginRequest request = UserLoginRequest.builder()
            .email(EMAIL)
            .password("strongPass1")
            .build();

        when(
            accountLoginService.execute(any(UserLoginRequest.class))
        ).thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc
            .perform(
                post("/account/auth")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(accountLoginService).execute(any(UserLoginRequest.class));
    }

    @Test
    void register_withValidPayload_returnsCreatedPayload() throws Throwable {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email(EMAIL)
            .password("strongPass1")
            .passwordConfirmation("strongPass1")
            .build();

        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        UserCreatedResponse createdResponse = new UserCreatedResponse(
            "John",
            "Doe",
            EMAIL,
            "verify-token",
            createdAt
        );

        when(
            accountRegisterService.execute(any(UserRegistrationRequest.class))
        ).thenReturn(createdResponse);

        mockMvc
            .perform(
                post("/account/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.message").value("Created"))
            .andExpect(jsonPath("$.info.token").value("verify-token"))
            .andExpect(
                jsonPath("$.info.createdAt").value(
                    createdAt.format(ISO_FORMATTER)
                )
            );

        verify(accountRegisterService).execute(
            any(UserRegistrationRequest.class)
        );
    }

    @Test
    void register_whenUserAlreadyExists_returnsBadRequest() throws Throwable {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email(EMAIL)
            .password("strongPass1")
            .passwordConfirmation("strongPass1")
            .build();

        when(
            accountRegisterService.execute(any(UserRegistrationRequest.class))
        ).thenThrow(new UserAlreadyExistsException(EMAIL));

        mockMvc
            .perform(
                post("/account/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(
                jsonPath("$.message").value(
                    String.format("User with email %s already exists", EMAIL)
                )
            );

        verify(accountRegisterService).execute(
            any(UserRegistrationRequest.class)
        );
    }

    @Test
    void register_whenIllegalArgumentThrown_returnsBadRequest()
        throws Throwable {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email(EMAIL)
            .password("strongPass1")
            .passwordConfirmation("differentPass")
            .build();

        when(
            accountRegisterService.execute(any(UserRegistrationRequest.class))
        ).thenThrow(
            new IllegalArgumentException("Password confirmation does not match")
        );

        mockMvc
            .perform(
                post("/account/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(
                jsonPath("$.message").value(
                    "Password confirmation does not match"
                )
            );

        verify(accountRegisterService).execute(
            any(UserRegistrationRequest.class)
        );
    }

    @Test
    void register_whenSecurityValidationFails_returnsUnauthorized()
        throws Throwable {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email(EMAIL)
            .password("strongPass1")
            .passwordConfirmation("strongPass1")
            .build();

        when(
            accountRegisterService.execute(any(UserRegistrationRequest.class))
        ).thenThrow(
            new SecurityValidationException("Registration blocked", null) {}
        );

        mockMvc
            .perform(
                post("/account/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Registration blocked"));

        verify(accountRegisterService).execute(
            any(UserRegistrationRequest.class)
        );
    }

    @Test
    void verify_withValidToken_returnsOkResponse() throws Throwable {
        LocalDateTime createdAt = LocalDateTime.of(2023, 12, 31, 23, 59);
        UserMinDetail detail = new UserMinDetail(EMAIL, createdAt);

        when(accountVerifiedService.execute("token-123")).thenReturn(detail);

        mockMvc
            .perform(get("/account/verify/token-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info.email").value(EMAIL))
            .andExpect(
                jsonPath("$.info.createdAt").value(
                    createdAt.format(ISO_FORMATTER)
                )
            );

        verify(accountVerifiedService).execute("token-123");
    }

    @Test
    void verify_whenIllegalArgumentThrown_returnsBadRequest() throws Throwable {
        when(accountVerifiedService.execute("bad-token")).thenThrow(
            new IllegalArgumentException("Invalid verification token")
        );

        mockMvc
            .perform(get("/account/verify/bad-token"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(
                jsonPath("$.message").value("Invalid verification token")
            );

        verify(accountVerifiedService).execute("bad-token");
    }

    @Test
    void verify_whenSecurityValidationFails_returnsUnauthorized()
        throws Throwable {
        when(accountVerifiedService.execute("locked-token")).thenThrow(
            new JwtTokenException("Verification denied")
        );

        mockMvc
            .perform(get("/account/verify/locked-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Verification denied"));

        verify(accountVerifiedService).execute("locked-token");
    }

    @Test
    void verify_whenDecodingFails_returnsInternalServerError()
        throws Throwable {
        when(accountVerifiedService.execute("broken-token")).thenThrow(
            new DecodingException(
                "broken-token",
                new IllegalArgumentException()
            )
        );

        mockMvc
            .perform(get("/account/verify/broken-token"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.message").value("Invalid token jwt config"));

        verify(accountVerifiedService).execute("broken-token");
    }

    @Test
    void verify_whenUnhandledExceptionThrown_returnsInternalServerError()
        throws Throwable {
        when(accountVerifiedService.execute("boom-token")).thenThrow(
            new RuntimeException("Unexpected")
        );

        mockMvc
            .perform(get("/account/verify/boom-token"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(
                jsonPath("$.message").value(
                    "An error occurred during execution"
                )
            );

        verify(accountVerifiedService).execute("boom-token");
    }
}
