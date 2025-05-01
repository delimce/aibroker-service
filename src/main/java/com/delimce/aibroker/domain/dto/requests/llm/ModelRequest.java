package com.delimce.aibroker.domain.dto.requests.llm;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
    String model;
    boolean stream;
    ModelMessageRequest[] messages;
    int temperature;
}
