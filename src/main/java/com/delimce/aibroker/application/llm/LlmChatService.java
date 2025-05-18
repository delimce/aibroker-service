package com.delimce.aibroker.application.llm;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.entities.UserRequest;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.repositories.UserRequestRepository;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.application.BaseService;

@Service
public class LlmChatService extends BaseService {

    private final ModelRepository modelRepository;
    private final UserRequestRepository userRequestRepository;
    private final AiApiClientInterface client;

    public LlmChatService(ModelRepository modelRepository, UserRequestRepository userRequestRepository,
            AiApiClientInterface client) {
        this.modelRepository = modelRepository;
        this.userRequestRepository = userRequestRepository;
        this.client = client;
    }

    @Transactional(rollbackFor = Exception.class)
    public ModelChatResponse execute(ModelRequest request) {

        Model model = modelRepository.findByName(request.getModel());

        if (model == null) {
            throw new IllegalArgumentException("Model not found");
        }

        if (!model.isEnabled()) {
            throw new IllegalArgumentException("Model is not enabled");
        }

        User user = fetchAuthenticatedUser();

        // save request data before send to model
        UserRequest userRequest = UserRequest.builder()
                .model(model)
                .user(user)
                .prompt(request.getMessages()[0].getContent())
                .build();

        // Save the user request to the database
        userRequestRepository.save(userRequest);

        ModelChatResponse chatResponse = client.requestToModel(model, request);

        if (chatResponse == null) {
            throw new IllegalArgumentException("Chat response is null");
        }

        return chatResponse;

    }

    protected void processStats(ModelChatResponse chatResponse) {
    }

}
