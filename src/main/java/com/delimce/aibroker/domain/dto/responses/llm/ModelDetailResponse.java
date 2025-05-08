package com.delimce.aibroker.domain.dto.responses.llm;

import java.time.LocalDateTime;

import com.delimce.aibroker.domain.enums.ModelType;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ModelDetailResponse {

    String name;
    String provider;
    ModelType type;
    boolean enabled;
    LocalDateTime createdAt;
}
