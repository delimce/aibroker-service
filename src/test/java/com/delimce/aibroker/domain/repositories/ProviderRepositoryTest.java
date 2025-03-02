package com.delimce.aibroker.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.utils.TestHandler;

class ProviderRepositoryTest extends TestHandler {

    @Autowired
    private ProviderRepository providerRepository;

    @Test
    void shouldSaveProvider() {
        // Given
        Provider provider = new Provider();
        var name = faker().name().name();
        var apiKey = faker().internet().password();
        provider.setName(name);
        provider.setDescription(faker().lorem().sentence());
        provider.setEnabled(false);
        provider.setApiKey(apiKey);
        provider.setBaseUrl(faker().internet().url());

        // When
        Provider savedProvider = providerRepository.save(provider);

        // Then
        assertThat(savedProvider.getId()).isNotNull();
        assertThat(savedProvider.getName()).isEqualTo(name);
        assertThat(savedProvider.getApiKey()).isEqualTo(apiKey);
    }

    @Test
    void shouldFindProviderById() {
        // Given
        Provider provider = new Provider();
        var name = faker().name().name();
        var apiKey = faker().internet().password();
        provider.setName(name);
        provider.setDescription(faker().lorem().sentence());
        provider.setApiKey(apiKey);
        provider.setBaseUrl(faker().internet().url());
        Provider savedProvider = providerRepository.save(provider);

        // When
        var foundProvider = providerRepository.findById(savedProvider.getId());

        // Then
        assertThat(foundProvider).isPresent();
        assertThat(foundProvider.get().getId()).isEqualTo(savedProvider.getId());
        assertThat(foundProvider.get().getName()).isEqualTo(name);
        assertThat(foundProvider.get().getApiKey()).isEqualTo(apiKey);
    }

    @Test
    void shouldSaveProviderAndModel() {
        // Given
        Provider provider = new Provider();
        var name = faker().name().name();
        var apiKey = faker().internet().password();
        provider.setName(name);
        provider.setDescription(faker().lorem().sentence());
        provider.setEnabled(false);
        provider.setApiKey(apiKey);
        provider.setBaseUrl(faker().internet().url());

        // generate list of Models
        List<Model> models = List.of(
                Model.builder().name(faker().name().name()).enabled(true).provider(provider).build(),
                Model.builder().name(faker().name().name()).enabled(true).provider(provider).build(),
                Model.builder().name(faker().name().name()).enabled(true).provider(provider).build());

        provider.setModels(models);

        // When
        Provider savedProvider = providerRepository.save(provider);

        // Then
        assertThat(savedProvider.getId()).isNotNull();
        assertThat(savedProvider.getName()).isEqualTo(name);
        assertThat(savedProvider.getApiKey()).isEqualTo(apiKey);
        assertThat(savedProvider.getModels()).isNotEmpty();
        assertThat(savedProvider.getModels().size()).isEqualTo(3);

      
    }
}