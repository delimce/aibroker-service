package com.delimce.aibroker.infrastructure.controllers.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomErrorControllerTest {

    // No logger dependency: controller uses Lombok @Log4j2 internally

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customErrorController = new CustomErrorController();
    }

    @Test
    void handleError_WithStatusCode404_ReturnsNotFoundMessage() {
        // Arrange
        when(requestMock.getAttribute("javax.servlet.error.status_code")).thenReturn(HttpStatus.NOT_FOUND.value());

        // Act
        String result = customErrorController.handleError(requestMock, responseMock);

        // Assert
        assertEquals("404 - Resource not found.", result);
        verify(responseMock).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleError_WithStatusCode500_ReturnsGenericErrorMessage() {
        // Arrange
        when(requestMock.getAttribute("javax.servlet.error.status_code"))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Act
        String result = customErrorController.handleError(requestMock, responseMock);

        // Assert
        assertEquals("An error occurred. Please try again later.", result);
        verify(responseMock).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void handleError_WithOtherStatusCode_ReturnsGenericErrorMessage() {
        // Arrange
        when(requestMock.getAttribute("javax.servlet.error.status_code")).thenReturn(HttpStatus.BAD_REQUEST.value());

        // Act
        String result = customErrorController.handleError(requestMock, responseMock);

        // Assert
        assertEquals("An error occurred. Please try again later.", result);
        verify(responseMock).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void handleError_WhenExceptionOccurs_LogsErrorAndReturnsNotFoundMessage() {
        // Arrange
        String exceptionMessage = "Test exception";
        when(requestMock.getAttribute("javax.servlet.error.status_code"))
                .thenThrow(new RuntimeException(exceptionMessage));

        // Act
        String result = customErrorController.handleError(requestMock, responseMock);

        // Assert
        assertEquals("404 - Resource not found.", result);
        verify(responseMock).setStatus(HttpStatus.NOT_FOUND.value());
        // Logging is internal to the controller (Lombok @Log4j2); we don't assert on
        // the logger here.
    }
}