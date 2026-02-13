package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.enums.PromptMessageType;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;

import lombok.AllArgsConstructor;

import com.delimce.aibroker.domain.mappers.llm.ChatResponseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeepSeekAdapter implements AiApiClientInterface {
        private final ChatResponseMapper chatResponseMapper;

        @Override
        public ModelChatResponse requestToModel(
                        Model model,
                        ModelRequest modelRequest) {
                DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                                .apiKey(model.getProvider().getApiKey())
                                .build();
                DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                                .model(DeepSeekApi.ChatModel.DEEPSEEK_CHAT.getValue())
                                .temperature(modelRequest.getTemperature())
                                .maxTokens(200)
                                .build();

                DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                                .deepSeekApi(deepSeekApi)
                                .defaultOptions(options)
                                .build();

                var messages = mergeMessages(modelRequest.getMessages());

                ChatResponse response = chatModel.call(new Prompt(messages));

                return mapToModelChatResponse(response);
        }

        @SuppressWarnings("null")
        protected List<Message> mergeMessages(ModelMessageRequest[] messageRequests) {
                List<Message> messages = new ArrayList<>();
                if (messageRequests != null && messageRequests.length > 0) {
                        messages = java.util.Arrays.stream(messageRequests)
                                        .filter(Objects::nonNull)
                                        .<Message>map(messageRequest -> {
                                                PromptMessageType type = messageRequest.getRole() != null
                                                                ? PromptMessageType.valueOf(messageRequest.getRole())
                                                                : PromptMessageType.user;
                                                String content = messageRequest.getContent();

                                                return switch (type) {
                                                        case system -> new SystemMessage(content);
                                                        case assistant -> new AssistantMessage(content);
                                                        case user -> new UserMessage(content);
                                                };
                                        })
                                        .toList();
                }

                return messages;
        }

        protected ModelChatResponse mapToModelChatResponse(ChatResponse response) {
                return chatResponseMapper.toModelChatResponse(response);
        }
}
