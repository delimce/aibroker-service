package com.delimce.aibroker.domain.dto.requests.llm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
    @NotBlank(message = "Model is required")
    @Size(min = 12, message = "Model must be at least 12 character long")
    String model;

    @Builder.Default
    boolean stream = false;

    @NotNull(message = "Messages array cannot be null")
    @NotEmpty(message = "Messages array cannot be empty")
    @Valid
    ModelMessageRequest[] messages;

    @Builder.Default
    int temperature = 1;
}
