package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.mappers.llm.ChatResponseMapper;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAiAdapter implements AiApiClientInterface {

    private ChatResponseMapper chatResponseMapper;

    public OpenAiAdapter(ChatResponseMapper chatResponseMapper) {
        this.chatResponseMapper = chatResponseMapper;
    }

    @Override
    public ModelChatResponse requestToModel(Model model, ModelRequest modelRequest) {
        OpenAiApi openAiApi = new OpenAiApi(
                model.getProvider().getBaseUrl(),
                model.getProvider().getApiKey());

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(modelRequest.getModel())
                .withTemperature(modelRequest.getTemperature())
                .withMaxTokens(200)
                .build();

        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        // Convert all messages from request to Spring AI Message objects
        List<Message> messages = new ArrayList<>();
        for (ModelMessageRequest msgReq : modelRequest.getMessages()) {
            if ("system".equalsIgnoreCase(msgReq.getRole())) {
                messages.add(new SystemMessage(msgReq.getContent()));
            } else if ("assistant".equalsIgnoreCase(msgReq.getRole())) {
                messages.add(new AssistantMessage(msgReq.getContent()));
            } else {
                // Default to user message
                messages.add(new UserMessage(msgReq.getContent()));
            }
        }

        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);

        return chatResponseMapper.toModelChatResponse(response);
    }
}
