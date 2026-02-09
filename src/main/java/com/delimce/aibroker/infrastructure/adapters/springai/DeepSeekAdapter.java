package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.mappers.llm.ChatResponseMapper;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DeepSeekAdapter implements AiApiClientInterface {

        private ChatResponseMapper chatResponseMapper;

        public DeepSeekAdapter(
                        ChatResponseMapper chatResponseMapper) {
                this.chatResponseMapper = chatResponseMapper;
        }

        @Override
        public ModelChatResponse requestToModel(
                        Model model,
                        ModelRequest modelRequest) {
                DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                                .baseUrl(model.getProvider().getBaseUrl())
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

                Prompt prompt = new Prompt(modelRequest.getMessages()[1].getContent());
                ChatResponse response = chatModel.call(prompt);

                System.out.println(response.getResult());

                return chatResponseMapper.toModelChatResponse(response);
        }
}
