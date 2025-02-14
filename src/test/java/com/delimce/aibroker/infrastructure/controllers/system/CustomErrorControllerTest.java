package com.delimce.aibroker.infrastructure.controllers.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CustomErrorController.class)
public class CustomErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private HttpServletRequest request;

    @Test
    public void handleError_shouldReturnNotFoundMessage_whenStatusIs404() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value());

        mockMvc.perform(MockMvcRequestBuilders.get("/error").requestAttr("javax.servlet.error.status_code",
                HttpStatus.NOT_FOUND.value()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("404 - Resource not found."));
    }

    @Test
    public void handleError_shouldReturnGenericErrorMessage_whenStatusIsNot404() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());

        mockMvc.perform(MockMvcRequestBuilders.get("/error").requestAttr("javax.servlet.error.status_code",
                HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred. Please try again later."));
    }
}
