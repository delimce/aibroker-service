package com.delimce.aibroker.application.llm;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.domain.enums.ModelType;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmChatServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private AiApiClientInterface client;

    @InjectMocks
    private LlmChatService llmChatService;

    @Test
    void execute_shouldReturnChatResponse_whenModelExistsAndIsEnabled() {
        // Arrange
        ModelRequest request = new ModelRequest();
        request.setModel("TestModel");

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
        when(client.requestToModel(model, request)).thenReturn(expectedResponse);

        // Act
        ModelChatResponse actualResponse = llmChatService.execute(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void execute_shouldThrowIllegalArgumentException_whenModelNotFound() {
        // Arrange
        ModelRequest request = new ModelRequest();
        request.setModel("NonExistentModel");

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
        ModelRequest request = new ModelRequest();
        request.setModel("DisabledModel");

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
        ModelRequest request = new ModelRequest();
        request.setModel("TestModel");

        Provider provider = new Provider();
        provider.setName("TestProvider");

        Model model = new Model();
        model.setName("TestModel");
        model.setProvider(provider);
        model.setType(ModelType.CHAT);
        model.setEnabled(true);
        model.setCreatedAt(LocalDateTime.now());

        when(modelRepository.findByName("TestModel")).thenReturn(model);
        when(client.requestToModel(model, request)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            llmChatService.execute(request);
        });
        assertEquals("Chat response is null", exception.getMessage());
    }
}
