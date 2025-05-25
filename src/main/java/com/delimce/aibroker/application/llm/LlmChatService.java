package com.delimce.aibroker.application.llm;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.RequestMetric;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.entities.UserRequest;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.repositories.RequestMetricRepository;
import com.delimce.aibroker.domain.repositories.UserRequestRepository;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.dto.responses.llm.Usage;
import com.delimce.aibroker.application.BaseService;

@Service
public class LlmChatService extends BaseService {

    private final ModelRepository modelRepository;
    private final UserRequestRepository userRequestRepository;
    private final RequestMetricRepository requestMetricRepository;
    private final AiApiClientInterface client;

    public LlmChatService(ModelRepository modelRepository, UserRequestRepository userRequestRepository,
            RequestMetricRepository requestMetricRepository, AiApiClientInterface client) {
        this.modelRepository = modelRepository;
        this.userRequestRepository = userRequestRepository;
        this.requestMetricRepository = requestMetricRepository;
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

        // Process and save metrics from the response
        processStats(chatResponse, userRequest);

        return chatResponse;

    }

    /**
     * Process and save token usage metrics from the model response
     * 
     * @param chatResponse The model chat response
     * @param userRequest  The user request
     */
    protected void processStats(ModelChatResponse chatResponse, UserRequest userRequest) {
        // Check if usage data is available
        if (chatResponse.getUsage() != null) {
            Usage usage = chatResponse.getUsage();

            // Create a new RequestMetric entity
            RequestMetric metric = RequestMetric.builder()
                    .userRequest(userRequest)
                    .promptTokens(usage.getPrompt_tokens())
                    .completionTokens(usage.getCompletion_tokens())
                    .totalTokens(usage.getTotal_tokens())
                    .promptCacheHitTokens(usage.getPrompt_cache_hit_tokens())
                    .promptCacheMissTokens(usage.getPrompt_cache_miss_tokens())
                    .build();

            // Save the metrics to the database
            requestMetricRepository.save(metric);
        }
    }

}
