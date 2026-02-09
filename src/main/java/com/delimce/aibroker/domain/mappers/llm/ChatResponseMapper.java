package com.delimce.aibroker.domain.mappers.llm;

import org.mapstruct.Mapper;
import org.springframework.ai.chat.model.ChatResponse;

import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;

@Mapper(componentModel = "spring")
public interface ChatResponseMapper {

    ModelChatResponse toModelChatResponse(ChatResponse chatResponse);

}
