package com.delimce.aibroker.domain.mappers.llm;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import com.delimce.aibroker.domain.dto.responses.llm.Choice;
import com.delimce.aibroker.domain.dto.responses.llm.Message;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.dto.responses.llm.Usage;

@Mapper(componentModel = "spring")
public interface ChatResponseMapper {

    @Mapping(target = "id", expression = "java(getId(chatResponse))")
    @Mapping(target = "object", constant = "chat.completion")
    @Mapping(target = "created", expression = "java(getCreated(chatResponse))")
    @Mapping(target = "model", expression = "java(getModel(chatResponse))")
    @Mapping(target = "choices", expression = "java(mapChoices(chatResponse))")
    @Mapping(target = "usage", expression = "java(mapUsage(chatResponse))")
    @Mapping(target = "system_fingerprint", ignore = true)
    ModelChatResponse toModelChatResponse(ChatResponse chatResponse);

    default String getId(ChatResponse chatResponse) {
        var metadata = chatResponse.getMetadata();
        return metadata != null ? metadata.getId() : null;
    }

    default String getModel(ChatResponse chatResponse) {
        var metadata = chatResponse.getMetadata();
        return metadata != null ? metadata.getModel() : null;
    }

    default long getCreated(ChatResponse chatResponse) {
        var metadata = chatResponse.getMetadata();
        if (metadata == null) {
            return 0L;
        }
        Object createdValue = metadata.get("created");
        return createdValue instanceof Number number ? number.longValue() : 0L;
    }

    default Choice[] mapChoices(ChatResponse response) {
        var results = response.getResults();
        if (results == null || results.isEmpty()) {
            return new Choice[0];
        }

        Choice[] choices = new Choice[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Generation generation = results.get(i);
            var output = generation.getOutput();
            String role = output != null
                    ? output.getMessageType().getValue()
                    : "assistant";
            String content = output != null ? output.getText() : null;
            Message message = new Message(role, content);
            String finishReason = generation.getMetadata() != null
                    ? generation.getMetadata().getFinishReason()
                    : null;
            choices[i] = new Choice(i, message, null, finishReason);
        }

        return choices;
    }

    default Usage mapUsage(ChatResponse response) {
        var metadata = response.getMetadata();
        if (metadata == null || metadata.getUsage() == null) {
            return null;
        }
        var usage = metadata.getUsage();
        int promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens() : 0;
        int completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0;
        Integer totalTokensValue = usage.getTotalTokens();
        int totalTokens = totalTokensValue != null ? totalTokensValue : promptTokens + completionTokens;

        return new Usage(promptTokens, completionTokens, totalTokens, null, 0, 0);
    }

}
