package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.ports.LoggerInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebClientAdapterTest {

    @Mock
    private WebClient.Builder webClientBuilderMock;
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;
    @Mock
    private LoggerInterface loggerMock;

    private WebClientAdapter webClientAdapter;

    @BeforeEach
    void setUp() {
        when(webClientBuilderMock.build()).thenReturn(webClientMock);
        webClientAdapter = new WebClientAdapter(webClientBuilderMock, loggerMock);
    }

    @Test
    void ping_ShouldReturnTrue_WhenApiCallIsSuccessful() {
        // Arrange
        // Add necessary stubbings for this specific test case
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        ResponseEntity<Void> successResponse = ResponseEntity.ok().build();
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(successResponse));

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertTrue(result);
        verifyNoInteractions(loggerMock); // No errors or warnings should be logged
    }

    @Test
    void ping_ShouldReturnFalse_WhenApiCallReturnsNon2xxStatus() {
        // Arrange
        // Add necessary stubbings for this specific test case
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        ResponseEntity<Void> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // Simulate non-2xx status by returning a Mono that completes successfully but
        // with a non-2xx code
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(notFoundResponse));

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
        verifyNoInteractions(loggerMock); // onErrorResume handles this internally, logging is inside it
    }

    @Test
    void ping_ShouldReturnFalseAndLogWarning_WhenRetrieveThrowsWebClientResponseException() {
        // Arrange
        // Add necessary stubbings for this specific test case
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        WebClientResponseException webClientException = new WebClientResponseException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null, null, null);
        // Simulate an error during the retrieve step (e.g., 500 status)
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.error(webClientException));

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
        verify(loggerMock).warn(contains("Ping to https://httpbin.org/get failed: 500 Internal Server Error"));
        verify(loggerMock, never()).error(anyString(), any());
    }

    @Test
    void ping_ShouldReturnFalseAndLogError_WhenWebClientThrowsException() {
        // Arrange
        RuntimeException networkException = new RuntimeException("Connection refused");
        // Only stub the method that throws the exception for this test
        when(webClientMock.get()).thenThrow(networkException);

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
        verify(loggerMock).error(contains("Unexpected error during ping"), contains("https://httpbin.org/get"),
                contains("Connection refused"), eq(networkException));
        verify(loggerMock, never()).warn(anyString());
    }
}
