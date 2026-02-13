package com.delimce.aibroker.domain.ports;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;

public interface AiApiClientInterface {

    ModelChatResponse requestToModel(Model model, ModelRequest modelRequest);
}
