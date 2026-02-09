package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.mappers.llm.ChatResponseMapper;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

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

        Prompt prompt = new Prompt(modelRequest.getMessages()[1].getContent());
        ChatResponse response = chatModel.call(prompt);

        System.out.println(response.getResult());

        return chatResponseMapper.toModelChatResponse(response);
    }
}
