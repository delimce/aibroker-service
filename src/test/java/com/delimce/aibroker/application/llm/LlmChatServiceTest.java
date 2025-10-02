package com.delimce.aibroker.application.llm;

import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.entities.UserRequest;
import com.delimce.aibroker.domain.enums.ModelType;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.repositories.RequestMetricRepository;
import com.delimce.aibroker.domain.repositories.UserRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmChatServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private UserRequestRepository userRequestRepository;

    @Mock
    private RequestMetricRepository requestMetricRepository;

    @Mock
    private AiApiClientInterface client;

    private User testUser;
    private TestLlmChatService llmChatService;

    // Testable subclass that allows us to override the protected method
    class TestLlmChatService extends LlmChatService {
        private User mockUser;

        public TestLlmChatService(ModelRepository modelRepository,
                UserRequestRepository userRequestRepository,
                RequestMetricRepository requestMetricRepository,
                AiApiClientInterface client,
                User mockUser) {
            super(modelRepository, userRequestRepository, requestMetricRepository, client);
            this.mockUser = mockUser;
        }

        @Override
        protected User fetchAuthenticatedUser() {
            return mockUser;
        }
    }

    @BeforeEach
    void setUp() {
        // Create a test user for all tests
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .lastName("Doe")
                .email("test@example.com")
                .status(UserStatus.ACTIVE)
                .build();

        // Initialize the service with our test user
        llmChatService = new TestLlmChatService(
                modelRepository,
                userRequestRepository,
                requestMetricRepository,
                client,
                testUser);
    }

    @Test
    void execute_shouldReturnChatResponse_whenModelExistsAndIsEnabled() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("TestModel", false, messages, 1);

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        ModelChatResponse expectedResponse = new ModelChatResponse();
        // Populate expectedResponse as needed

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        when(userRequestRepository.save(any(UserRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(client.requestToModel(model, request)).thenReturn(expectedResponse);

        // Act
        ModelChatResponse actualResponse = llmChatService.execute(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        // Verify userRequestRepository.save was called once
        verify(userRequestRepository).save(any(UserRequest.class));
    }

    @Test
    void execute_shouldThrowIllegalArgumentException_whenModelNotFound() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("NonExistentModel", false, messages, 1);

        when(modelRepository.findByName("NonExistentModel")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            llmChatService.execute(request);
        });
        assertEquals("Model not found", exception.getMessage());
    }

    @Test
    void execute_shouldThrowIllegalArgumentException_whenModelNotEnabled() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("DisabledModel", false, messages, 1);

        Model model = new Model();
        model.setName("DisabledModel");
        model.setEnabled(false);

        when(modelRepository.findByName("DisabledModel")).thenReturn(model);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            llmChatService.execute(request);
        });
        assertEquals("Model is not enabled", exception.getMessage());
    }

    @Test
    void execute_shouldThrowIllegalArgumentException_whenChatResponseIsNull() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("TestModel", false, messages, 1);

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        when(userRequestRepository.save(any(UserRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(client.requestToModel(model, request)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            llmChatService.execute(request);
        });
        assertEquals("Chat response is null", exception.getMessage());
        // Verify userRequestRepository.save was called once
        verify(userRequestRepository).save(any(UserRequest.class));
    }

    @Test
    void execute_shouldRollbackTransaction_whenExceptionOccurs() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("TestModel", false, messages, 1);

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        // Simulate database error when saving
        when(userRequestRepository.save(any(UserRequest.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            llmChatService.execute(request);
        });
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void execute_shouldSaveUsageMetrics_whenResponseHasUsageData() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("TestModel", false, messages, 1);

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        // Create a response with usage data
        com.delimce.aibroker.domain.dto.responses.llm.Usage usage = new com.delimce.aibroker.domain.dto.responses.llm.Usage(
                100, 50, 150, null, 20, 80);
        ModelChatResponse expectedResponse = new ModelChatResponse(null, null, 0, null, null, usage, null);

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        when(userRequestRepository.save(any(UserRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(client.requestToModel(model, request)).thenReturn(expectedResponse);
        // Use mockito answer to capture the RequestMetric being saved
        when(requestMetricRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        ModelChatResponse actualResponse = llmChatService.execute(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(userRequestRepository).save(any(UserRequest.class));
        verify(requestMetricRepository).save(any(com.delimce.aibroker.domain.entities.RequestMetric.class));
    }

    @Test
    void execute_shouldNotSaveMetrics_whenResponseHasNoUsageData() {
        // Arrange
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello, how are you?")
        };

        ModelRequest request = new ModelRequest("TestModel", false, messages, 1);

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        // Create a response with no usage data
        ModelChatResponse expectedResponse = new ModelChatResponse(null, null, 0, null, null, null, null);

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        when(userRequestRepository.save(any(UserRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(client.requestToModel(model, request)).thenReturn(expectedResponse);

        // Act
        ModelChatResponse actualResponse = llmChatService.execute(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(userRequestRepository).save(any(UserRequest.class));
        // Verify that requestMetricRepository.save is never called
        verify(requestMetricRepository, org.mockito.Mockito.never()).save(any());
    }
}
