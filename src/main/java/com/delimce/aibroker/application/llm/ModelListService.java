package com.delimce.aibroker.application.llm;

import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.dto.responses.llm.ModelDetailResponse;
import com.delimce.aibroker.domain.repositories.ModelRepository;

@Service
public class ModelListService {

    private final ModelRepository modelRepository;

    public ModelListService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public ModelDetailResponse[] execute() {
        return modelRepository.findAll().stream()
                .map(model -> new ModelDetailResponse(
                        model.getName(),
                        model.getProvider().getName(),
                        model.getType(),
                        model.isEnabled(),
                        model.getCreatedAt()))
                .toArray(ModelDetailResponse[]::new);

    }

}
