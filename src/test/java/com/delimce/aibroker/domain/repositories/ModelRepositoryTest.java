package com.delimce.aibroker.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.domain.enums.ModelType;
import com.delimce.aibroker.utils.TestHandler;

public class ModelRepositoryTest extends TestHandler {

    protected Provider aiProvider;

    @BeforeEach
    public void setUp() {
        super.setUp();
        aiProvider = createProvider();
    }

    protected Provider createProvider() {
        Provider provider = new Provider();
        provider.setName("OpenAI");
        provider.setDescription("Artificial Intelligence");
        provider.setBaseUrl("https://openai.com");
        provider.setApiKey("api-token-here");
        provider.setEnabled(true);

        return providerRepository.save(provider);
    }

    @Test
    public void shouldSaveModel() {
        // Given
        Model model = new Model();
        model.setName("GPT-4");
        model.setType(ModelType.CHAT);
        model.setProvider(aiProvider);
        model.setEnabled(true);

        // When
        Model savedModel = modelRepository.save(model);

        // Then
        assertThat(savedModel.getId()).isNotNull();
        assertThat(savedModel.getName()).isEqualTo("GPT-4");
    }

    @SuppressWarnings("null")
    @Test
    public void shouldFindModelById() {
        // Given
        Model model = new Model();
        model.setName("BERT");
        model.setType(ModelType.CHAT);
        model.setProvider(aiProvider);

        Long id = modelRepository.save(model).getId();

        // When
        Optional<Model> found = modelRepository.findById(id);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("BERT");
    }

    @SuppressWarnings("null")
    @Test
    public void shouldDeleteModel() {
        // Given
        Model model = new Model();
        model.setName("LLaMA");
        model.setType(ModelType.CHAT);
        model.setProvider(aiProvider);

        Long id = modelRepository.save(model).getId();

        // When
        modelRepository.deleteById(id);
        Optional<Model> deleted = modelRepository.findById(id);

        // Then
        assertThat(deleted).isEmpty();
    }

    @Test
    public void shouldFindModelByName() {
        // Given
        String modelName = "GPT-3.5";
        Model model = new Model();
        model.setName(modelName);
        model.setType(ModelType.CHAT);
        model.setProvider(aiProvider);
        model.setEnabled(true);
        modelRepository.save(model);

        // When
        Model foundModel = modelRepository.findByName(modelName);

        // Then
        assertThat(foundModel).isNotNull();
        assertThat(foundModel.getName()).isEqualTo(modelName);
        assertThat(foundModel.getProvider().getId()).isEqualTo(aiProvider.getId());
    }

    @Test
    public void shouldReturnNullWhenModelNameNotFound() {
        // Given
        String nonExistentName = "NonExistentModel";

        // When
        Model foundModel = modelRepository.findByName(nonExistentName);

        // Then
        assertThat(foundModel).isNull();
    }

}
