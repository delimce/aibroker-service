package com.delimce.aibroker.domain.dto.responses.llm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
    private int index;
    private Message message;
    @JsonIgnore
    private String logprobs;
    private String finish_reason;
}
