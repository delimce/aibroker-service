package com.delimce.aibroker.domain.dto.responses.llm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModelChatResponse {
    @JsonIgnore
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
    @JsonIgnore
    private String system_fingerprint;

}
