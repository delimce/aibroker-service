package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;

public class OpenAiAdapter implements AiApiClientInterface {

    @Override
    public ModelChatResponse requestToModel(Model model, ModelRequest modelRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestToModel'");
    }

}
