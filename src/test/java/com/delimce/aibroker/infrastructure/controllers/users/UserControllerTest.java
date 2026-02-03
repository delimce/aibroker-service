package com.delimce.aibroker.infrastructure.controllers.users;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delimce.aibroker.application.users.UserListService;
import com.delimce.aibroker.application.users.UserRefreshService;
import com.delimce.aibroker.domain.dto.responses.users.UserListResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserListService userListService;

        @MockitoBean
        private UserRefreshService userRefreshService;

        @MockitoBean
        private JwtTokenInterface jwtTokenInterface;

        @MockitoBean
        private UserRepository userRepository;

        @Test
        void listUsers_withValidAuthorizationHeader_returnsOk() throws Exception {
                List<UserListResponse> userResponses = List.of(
                                new UserListResponse(
                                                1L,
                                                "Jane",
                                                "Doe",
                                                "jane.doe@example.com",
                                                UserStatus.ACTIVE));

                when(userListService.execute()).thenReturn(userResponses);

                mockMvc.perform(get("/users/all").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("OK"))
                                .andExpect(jsonPath("$.info").isArray())
                                .andExpect(jsonPath("$.info.length()").value(1))
                                .andExpect(jsonPath("$.info[0].id").value(1))
                                .andExpect(jsonPath("$.info[0].email").value("jane.doe@example.com"))
                                .andExpect(jsonPath("$.info[0].status").value(UserStatus.ACTIVE.name()));

                verify(userListService).execute();
        }

        @Test
        void listUsers_withMultipleUsers_returnsAllUsers() throws Exception {
                List<UserListResponse> userResponses = List.of(
                                new UserListResponse(
                                                1L,
                                                "Jane",
                                                "Doe",
                                                "jane.doe@example.com",
                                                UserStatus.ACTIVE),
                                new UserListResponse(
                                                2L,
                                                "John",
                                                "Smith",
                                                "john.smith@example.com",
                                                UserStatus.ACTIVE),
                                new UserListResponse(
                                                3L,
                                                "Bob",
                                                "Johnson",
                                                "bob.johnson@example.com",
                                                UserStatus.INACTIVE));

                when(userListService.execute()).thenReturn(userResponses);

                mockMvc.perform(get("/users/all").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("OK"))
                                .andExpect(jsonPath("$.info").isArray())
                                .andExpect(jsonPath("$.info.length()").value(3))
                                .andExpect(jsonPath("$.info[0].id").value(1))
                                .andExpect(jsonPath("$.info[1].id").value(2))
                                .andExpect(jsonPath("$.info[2].id").value(3))
                                .andExpect(jsonPath("$.info[2].status").value(UserStatus.INACTIVE.name()));

                verify(userListService).execute();
        }

        @Test
        void listUsers_withEmptyList_returnsEmptyArray() throws Exception {
                when(userListService.execute()).thenReturn(List.of());

                mockMvc.perform(get("/users/all").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("OK"))
                                .andExpect(jsonPath("$.info").isArray())
                                .andExpect(jsonPath("$.info.length()").value(0));

                verify(userListService).execute();
        }

        @Test
        void listUsers_withServiceException_returnsInternalServerError()
                        throws Exception {
                when(userListService.execute()).thenThrow(new RuntimeException("boom"));

                mockMvc.perform(get("/users/all").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value("An error occurred during execution"));

                verify(userListService).execute();
        }

        @SuppressWarnings("null")
        @Test
        void refreshUserData_withValidAuthentication_returnsOk() throws Exception, JwtTokenException {
                UserLoggedResponse serviceResponse = new UserLoggedResponse(
                                "new-jwt-token",
                                "John",
                                "Doe",
                                "john@example.com");

                when(userRefreshService.execute()).thenReturn(serviceResponse);

                mockMvc.perform(put("/users/refresh").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("OK"))
                                .andExpect(jsonPath("$.info.token").value("new-jwt-token"))
                                .andExpect(jsonPath("$.info.name").value("John"))
                                .andExpect(jsonPath("$.info.lastName").value("Doe"))
                                .andExpect(jsonPath("$.info.email").value("john@example.com"));

                verify(userRefreshService).execute();
        }

        @SuppressWarnings("null")
        @Test
        void refreshUserData_withJwtTokenException_returnsUnauthorized()
                        throws Exception, JwtTokenException {
                when(userRefreshService.execute()).thenThrow(
                                new JwtTokenException("Token generation failed"));

                mockMvc.perform(put("/users/refresh").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.status").value(401))
                                .andExpect(jsonPath("$.message").value("Token generation failed"));

                verify(userRefreshService).execute();
        }

        @SuppressWarnings("null")
        @Test
        void refreshUserData_withUnexpectedException_returnsInternalServerError()
                        throws Exception, JwtTokenException {
                when(userRefreshService.execute()).thenThrow(
                                new RuntimeException("Unexpected error occurred"));

                mockMvc.perform(put("/users/refresh").header("Authorization", "Bearer validToken"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value(
                                                "An error occurred during execution"));

                verify(userRefreshService).execute();
        }
}
