package com.delimce.aibroker.infrastructure.controllers.llm;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delimce.aibroker.application.llm.LlmChatService;
import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.Choice;
import com.delimce.aibroker.domain.dto.responses.llm.Message;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.dto.responses.llm.Usage;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ChatRequestControllerTest.SecurityTestConfig.class)
class ChatRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LlmChatService llmChatService;

    @SuppressWarnings("null")
    @Test
    void chatRequest_whenValidPayload_returnsOkResponse() throws Exception {
        ModelMessageRequest messageRequest = ModelMessageRequest.builder()
            .role("user")
            .content("Hello!")
            .build();

        ModelRequest request = ModelRequest.builder()
            .model("gpt-4o-mini-2024")
            .messages(new ModelMessageRequest[] { messageRequest })
            .temperature(1)
            .stream(false)
            .build();

        Message assistantMessage = new Message("assistant", "Hi there!");
        Choice choice = new Choice(0, assistantMessage, null, "stop");
        Usage usage = new Usage(10, 15, 25, null, 2, 3);
        ModelChatResponse response = new ModelChatResponse(
            "chat-123",
            "chat.completion",
            Instant.parse("2024-01-01T10:15:30Z").getEpochSecond(),
            "gpt-4o-mini-2024",
            new Choice[] { choice },
            usage,
            "sig-xyz"
        );

        when(llmChatService.execute(any(ModelRequest.class))).thenReturn(
            response
        );

        mockMvc
            .perform(
                post("/llm/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info.object").value("chat.completion"))
            .andExpect(jsonPath("$.info.model").value("gpt-4o-mini-2024"))
            .andExpect(jsonPath("$.info.choices[0].index").value(0))
            .andExpect(
                jsonPath("$.info.choices[0].message.role").value("assistant")
            )
            .andExpect(
                jsonPath("$.info.choices[0].message.content").value("Hi there!")
            )
            .andExpect(jsonPath("$.info.usage.prompt_tokens").value(10))
            .andExpect(jsonPath("$.info.usage.completion_tokens").value(15))
            .andExpect(jsonPath("$.info.usage.total_tokens").value(25));

        ArgumentCaptor<ModelRequest> requestCaptor = ArgumentCaptor.forClass(
            ModelRequest.class
        );
        verify(llmChatService).execute(requestCaptor.capture());
        ModelRequest captured = requestCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(
            "gpt-4o-mini-2024",
            captured.getModel()
        );
        org.junit.jupiter.api.Assertions.assertFalse(captured.isStream());
        org.junit.jupiter.api.Assertions.assertEquals(
            1,
            captured.getTemperature()
        );
        org.junit.jupiter.api.Assertions.assertEquals(
            1,
            captured.getMessages().length
        );
        org.junit.jupiter.api.Assertions.assertEquals(
            "Hello!",
            captured.getMessages()[0].getContent()
        );
    }

    @SuppressWarnings("null")
    @Test
    void chatRequest_whenServiceThrowsIllegalArgument_returnsBadRequest()
        throws Exception {
        when(llmChatService.execute(any(ModelRequest.class))).thenThrow(
            new IllegalArgumentException("Model not found")
        );

        ModelRequest request = ModelRequest.builder()
            .model("gpt-4o-mini-2024")
            .messages(
                new ModelMessageRequest[] {
                    ModelMessageRequest.builder().content("Ping").build(),
                }
            )
            .build();

        mockMvc
            .perform(
                post("/llm/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Model not found"))
            .andExpect(jsonPath("$.info").value(nullValue()));

        verify(llmChatService).execute(any(ModelRequest.class));
    }

    @SuppressWarnings("null")
    @Test
    void chatRequest_whenServiceThrowsUnhandledException_returnsInternalServerError()
        throws Exception {
        when(llmChatService.execute(any(ModelRequest.class))).thenThrow(
            new RuntimeException("boom")
        );

        ModelRequest request = ModelRequest.builder()
            .model("gpt-4o-mini-2024")
            .messages(
                new ModelMessageRequest[] {
                    ModelMessageRequest.builder()
                        .content("Hello again")
                        .build(),
                }
            )
            .build();

        mockMvc
            .perform(
                post("/llm/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(
                jsonPath("$.message").value(
                    "An error occurred during execution"
                )
            )
            .andExpect(jsonPath("$.info").value(nullValue()));

        verify(llmChatService).execute(any(ModelRequest.class));
    }

    @SuppressWarnings("null")
    @Test
    void chatRequest_whenPayloadFailsValidation_returnsBadRequest()
        throws Exception {
        String payload = """
            {
              "model": "short",
              "messages": []
            }
            """;

        mockMvc
            .perform(
                post("/llm/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.model").value(
                    "Model must be at least 10 character long"
                )
            )
            .andExpect(
                jsonPath("$.messages").value("Messages array cannot be empty")
            );

        verify(llmChatService, never()).execute(any(ModelRequest.class));
    }

    @TestConfiguration
    static class SecurityTestConfig {

        @Bean
        JwtTokenInterface jwtTokenInterface() {
            return mock(JwtTokenInterface.class);
        }

        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}
