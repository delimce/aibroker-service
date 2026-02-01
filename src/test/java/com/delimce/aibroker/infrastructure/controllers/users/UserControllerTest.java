package com.delimce.aibroker.infrastructure.controllers.users;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delimce.aibroker.application.users.UserListService;
import com.delimce.aibroker.domain.dto.responses.users.UserListResponse;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserListService userListService;

    @MockBean
    private JwtTokenInterface jwtTokenInterface;

    @MockBean
    private UserRepository userRepository;

    @Test
    void listUsers_withoutAuthorizationHeader_returnsUnauthorized()
        throws Exception {
        mockMvc
            .perform(get("/users/all").header("Authorization", ""))
            .andExpect(status().isUnauthorized())
            .andExpect(
                jsonPath("$.message").value(
                    "Valid Authorization header with Bearer token is required"
                )
            )
            .andExpect(jsonPath("$.status").value(401));

        verify(userListService, never()).execute();
    }

    @Test
    void listUsers_withInvalidAuthorizationHeader_returnsUnauthorized()
        throws Exception {
        mockMvc
            .perform(get("/users/all").header("Authorization", "Token invalid"))
            .andExpect(status().isUnauthorized())
            .andExpect(
                jsonPath("$.message").value(
                    "Valid Authorization header with Bearer token is required"
                )
            )
            .andExpect(jsonPath("$.status").value(401));

        verify(userListService, never()).execute();
    }

    @Test
    void listUsers_withValidAuthorizationHeader_returnsOk() throws Exception {
        List<UserListResponse> userResponses = List.of(
            new UserListResponse(
                1L,
                "Jane",
                "Doe",
                "jane.doe@example.com",
                UserStatus.ACTIVE
            )
        );

        when(userListService.execute()).thenReturn(userResponses);

        mockMvc
            .perform(
                get("/users/all").header("Authorization", "Bearer validToken")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info").isArray())
            .andExpect(jsonPath("$.info.length()").value(1))
            .andExpect(jsonPath("$.info[0].id").value(1))
            .andExpect(
                jsonPath("$.info[0].email").value("jane.doe@example.com")
            )
            .andExpect(
                jsonPath("$.info[0].status").value(UserStatus.ACTIVE.name())
            );

        verify(userListService).execute();
    }

    @Test
    void listUsers_withServiceException_returnsInternalServerError()
        throws Exception {
        when(userListService.execute()).thenThrow(new RuntimeException("boom"));

        mockMvc
            .perform(
                get("/users/all").header("Authorization", "Bearer validToken")
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(
                jsonPath("$.message").value(
                    "An error occurred during execution"
                )
            );

        verify(userListService).execute();
    }
}
