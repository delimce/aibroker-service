package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.domain.ports.LoggerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Add class-level suppression for rawtypes and necessary unchecked operations
@SuppressWarnings({ "rawtypes", "unchecked" })
@ExtendWith(MockitoExtension.class)
class WebClientAdapterTest {

    @Mock
    private WebClient.Builder webClientBuilderMock;
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock; // Raw type
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock; // Raw type
    @Mock
    private WebClient.ResponseSpec responseSpecMock;
    @Mock
    private LoggerInterface loggerMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;
    @Mock
    private WebClient.RequestHeadersSpec postRequestHeadersSpecMock; // Raw type

    // ObjectMapper for simulating JSON parsing
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebClientAdapter webClientAdapter;

    @BeforeEach
    void setUp() {
        // Mock the builder to return the client mock
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        // Mock the basic structure for GET requests (used in ping tests)
        // Use lenient() for mocks used across different test types (GET/POST)
        // or mock more specifically within each test if preferred.
        lenient().when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        lenient().when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        // Let individual tests handle the final retrieve() and response mocking for GET

        // Mock the basic structure for POST requests (used in requestToModel tests)
        lenient().when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        lenient().when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        lenient().when(requestBodySpecMock.header(anyString(), anyString())).thenReturn(requestBodySpecMock);
        lenient().when(requestBodySpecMock.contentType(any(MediaType.class))).thenReturn(requestBodySpecMock);
        // Mock body(...) to return a RequestHeadersSpec mock
        // Use a separate mock instance if the chain differs significantly from GET
        lenient().when(requestBodySpecMock.body(any(Mono.class), eq(ModelRequest.class)))
                .thenReturn(postRequestHeadersSpecMock);

        // Initialize the adapter
        webClientAdapter = new WebClientAdapter(webClientBuilderMock, loggerMock, objectMapper);
    }

    // --- Ping Tests (Adjusted for clarity and potential fixes) ---

    @Test
    void ping_ShouldReturnTrue_WhenApiCallIsSuccessful() {
        // Arrange
        // Mock the retrieve().toBodilessEntity() chain specifically for this test
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        ResponseEntity<Void> successResponse = ResponseEntity.ok().build();
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(successResponse));

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertTrue(result);
        verifyNoInteractions(loggerMock);
    }

    @Test
    void ping_ShouldReturnFalse_WhenApiCallReturnsNon2xxStatus() {
        // Arrange
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        ResponseEntity<Void> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // Simulate non-2xx status by returning a Mono that completes successfully but
        // with a non-2xx code
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(notFoundResponse));

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
    }

    @Test
    void ping_ShouldReturnFalseAndLogError_WhenRetrieveThrowsWebClientResponseException() {
        // Arrange
        WebClientResponseException webClientException = new WebClientResponseException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null, null, null);
        // Simulate retrieve throwing the exception
        when(requestHeadersSpecMock.retrieve()).thenThrow(webClientException); // Simulate exception earlier

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
        // Verify the logging within the catch block of the ping method
        verify(loggerMock).error(eq("Unexpected error during ping to {}: {}"), eq("https://httpbin.org/get"),
                eq("500 Internal Server Error"), eq(webClientException));
        verify(loggerMock, never()).warn(anyString()); // Should log error, not warning
    }

    @Test
    void ping_ShouldReturnFalseAndLogError_WhenWebClientGetThrowsException() {
        // Arrange
        RuntimeException networkException = new RuntimeException("Connection refused");
        // Override the setup from @BeforeEach for this specific case
        when(webClientMock.get()).thenThrow(networkException); // Exception happens before retrieve()

        // Act
        boolean result = webClientAdapter.ping();

        // Assert
        assertFalse(result);
        // Verify the logging within the catch block
        verify(loggerMock).error(eq("Unexpected error during ping to {}: {}"), eq("https://httpbin.org/get"),
                eq("Connection refused"), eq(networkException));
        verify(loggerMock, never()).warn(anyString());
    }

    // --- requestToModel Tests ---

    @Test
    void requestToModel_ShouldReturnResponse_WhenApiCallIsSuccessful() throws JsonProcessingException {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").apiKey("test-key").build();
        Model model = Model.builder().provider(provider).name("test-model").build();

        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();
        ModelChatResponse expectedResponse = new ModelChatResponse(); // Populate if needed
        String expectedJsonResponse = objectMapper.writeValueAsString(expectedResponse);

        // Mock the retrieve().bodyToMono() chain for POST
        // retrieve() is called on the result of body(...), which we mocked as
        // requestHeadersSpecMock
        when(postRequestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(expectedJsonResponse));

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNotNull(actualResponse);
        // Add more specific assertions based on expectedResponse content if necessary
        verify(loggerMock, never()).error(anyString(), any());
        verify(loggerMock, never()).warn(anyString());
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri("http://test-llm.com/api");
        verify(requestBodySpecMock).header(HttpHeaders.AUTHORIZATION, "Bearer test-key");
        verify(requestBodySpecMock).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpecMock).body(any(Mono.class), eq(ModelRequest.class)); // Check body insertion
        verify(postRequestHeadersSpecMock).retrieve();
        verify(responseSpecMock).bodyToMono(String.class);
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenApiReturnsErrorStatus() {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").apiKey("test-key").build();
        Model model = Model.builder().provider(provider).name("test-model").build();

        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();

        WebClientResponseException webClientException = new WebClientResponseException(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", null, "Invalid input".getBytes(), null);

        // Mock retrieve() to return a ResponseSpec that throws error on bodyToMono
        when(postRequestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(webClientException));

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error(
                eq("Request to model {} at {} failed with status {}: {}. Response body: {}"),
                eq("test-model"),
                eq("http://test-llm.com/api"),
                eq(HttpStatus.BAD_REQUEST),
                contains("Bad Request"), // Check message part
                eq("Invalid input"), // Check response body part
                eq(webClientException) // Check exception
        );
        verify(loggerMock, never()).info(anyString());
        verify(loggerMock, never()).warn(anyString());
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenOtherWebClientExceptionOccurs() {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").apiKey("test-key").build();
        Model model = Model.builder().provider(provider).name("test-model").build();

        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();
        RuntimeException networkException = new RuntimeException("Network Error");

        // Mock retrieve() to return a ResponseSpec that throws error on bodyToMono
        when(postRequestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(networkException));

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error(
                eq("Request to model {} at {} failed due to unexpected error: {}"),
                eq("test-model"),
                eq("http://test-llm.com/api"),
                eq("Network Error"),
                eq(networkException));
        verify(loggerMock, never()).info(anyString());
        verify(loggerMock, never()).warn(anyString());
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenResponseParsingFails() throws JsonProcessingException {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").apiKey("test-key").build();
        Model model = Model.builder().provider(provider).name("test-model").build();

        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();
        String invalidJsonResponse = "{\"invalid json"; // Malformed JSON

        // Mock the retrieve().bodyToMono() chain for POST
        when(postRequestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(invalidJsonResponse));

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock, never()).warn(anyString());
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenModelIsNull() {
        // Arrange
        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(null, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error("Cannot request model: Model or its Provider is null.");
        verifyNoInteractions(webClientMock); // No web client calls should be made
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenProviderIsNull() {
        // Arrange
        Model model = Model.builder().name("test-model").build(); // No provider set
        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error("Cannot request model: Model or its Provider is null.");
        verifyNoInteractions(webClientMock);
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenProviderUrlIsMissing() {
        // Arrange
        Provider provider = Provider.builder().apiKey("test-key").build(); // No URL set
        Model model = Model.builder().provider(provider).name("test-model").build();
        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error("Cannot request model '{}': Provider URL or API Key is missing.", "test-model");
        verifyNoInteractions(webClientMock);
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenProviderApiKeyIsMissing() {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").build(); // No API Key set
        Model model = Model.builder().provider(provider).name("test-model").build();
        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error("Cannot request model '{}': Provider URL or API Key is missing.", "test-model");
        verifyNoInteractions(webClientMock);
    }

    @Test
    void requestToModel_ShouldReturnNullAndLogError_WhenSynchronousExceptionOccurs() {
        // Arrange
        Provider provider = Provider.builder().baseUrl("http://test-llm.com/api").apiKey("test-key").build();
        Model model = Model.builder().provider(provider).name("test-model").build();

        ModelRequest request = ModelRequest.builder()
                .model("test-model")
                .messages(new ModelMessageRequest[] { new ModelMessageRequest("user", "Say hi") })
                .stream(false)
                .build();
        RuntimeException syncException = new RuntimeException("Synchronous Error");

        // Mock the start of the chain to throw an exception immediately
        when(webClientMock.post()).thenThrow(syncException);

        // Act
        ModelChatResponse actualResponse = webClientAdapter.requestToModel(model, request);

        // Assert
        assertNull(actualResponse);
        verify(loggerMock).error(
                eq("Unexpected synchronous error during requestToModel for model {}: {}"),
                eq("test-model"),
                eq("Synchronous Error"),
                eq(syncException));
        verify(loggerMock, never()).info(anyString());
        verify(loggerMock, never()).warn(anyString());
    }
}
