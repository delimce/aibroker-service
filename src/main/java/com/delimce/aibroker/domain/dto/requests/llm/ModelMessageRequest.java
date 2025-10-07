package com.delimce.aibroker.domain.dto.requests.llm;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ModelMessageRequest {

    @Builder.Default
    String role = "user";

    @NotBlank(message = "Content is required")
    String content;
}
