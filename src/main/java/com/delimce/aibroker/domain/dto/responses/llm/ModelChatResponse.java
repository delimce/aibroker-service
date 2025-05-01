package com.delimce.aibroker.domain.dto.responses.llm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelChatResponse {
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;

}
