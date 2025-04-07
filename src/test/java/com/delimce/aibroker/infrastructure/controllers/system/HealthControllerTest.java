package com.delimce.aibroker.infrastructure.controllers.system;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import com.delimce.aibroker.config.SecurityConfig;
import com.delimce.aibroker.utils.TestConfig;

@WebMvcTest(HealthController.class)
@Import({ SecurityConfig.class, TestConfig.class })
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsOk() throws Exception {
        // Health endpoint should be permitted without authentication
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }
}