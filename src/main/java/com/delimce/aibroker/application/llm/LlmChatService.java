package com.delimce.aibroker.application.llm;

import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;

@Service
public class LlmChatService {

    private final ModelRepository modelRepository;
    private final AiApiClientInterface client;

    public LlmChatService(ModelRepository modelRepository, AiApiClientInterface client) {
        this.modelRepository = modelRepository;
        this.client = client;
    }

    public ModelChatResponse execute(ModelRequest request) {

        Model model = modelRepository.findByName(request.getModel());
        if (model == null) {
            throw new IllegalArgumentException("Model not found");
        }

        if (!model.isEnabled()) {
            throw new IllegalArgumentException("Model is not enabled");
        }

        ModelChatResponse chatResponse = client.requestToModel(model, request);

        if (chatResponse == null) {
            throw new IllegalArgumentException("Chat response is null");
        }

        return chatResponse;

    }

    protected void processStats(ModelChatResponse chatResponse) {
    }

}
