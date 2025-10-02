package com.delimce.aibroker.domain.dto.responses.llm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    private String content;
}
