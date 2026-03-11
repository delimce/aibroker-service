package com.delimce.aibroker.domain.dto.requests.llm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ModelMessageRequest {

    @Builder.Default
    String role = "user";

    @Size(max = 800, message = "Content must be less than 800 characters")
    @NotBlank(message = "Content is required")
    String content;
}
