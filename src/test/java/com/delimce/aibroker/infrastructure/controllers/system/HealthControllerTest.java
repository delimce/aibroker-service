package com.delimce.aibroker.infrastructure.controllers.system;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import com.delimce.aibroker.config.SecurityConfig;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.config.JwtAuthenticationFilter;

import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenInterface jwtTokenInterface;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void healthEndpointReturnsOk() throws Exception {
        // Health endpoint should be permitted without authentication
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }
}