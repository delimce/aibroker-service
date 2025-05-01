package com.delimce.aibroker.domain.dto.requests.llm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ModelMessageRequest {
    String role;
    String content;
}
