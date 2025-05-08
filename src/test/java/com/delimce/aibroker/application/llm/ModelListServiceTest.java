package com.delimce.aibroker.application.llm;

import com.delimce.aibroker.domain.dto.responses.llm.ModelDetailResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.Provider;
import com.delimce.aibroker.domain.enums.ModelType;
import com.delimce.aibroker.domain.repositories.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelListServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelListService modelListService;

    @Test
    void execute_shouldReturnModelDetails() {
        // Arrange
        Provider provider1 = new Provider();
        provider1.setName("ProviderA");

        Model model1 = new Model();
        model1.setName("ModelX");
        model1.setProvider(provider1);
        model1.setType(ModelType.CHAT);
        model1.setEnabled(true);
        model1.setCreatedAt(LocalDateTime.now());

        Provider provider2 = new Provider();
        provider2.setName("ProviderB");

        Model model2 = new Model();
        model2.setName("ModelY");
        model2.setProvider(provider2);
        model2.setType(ModelType.EMBEDDING);
        model2.setEnabled(false);
        model2.setCreatedAt(LocalDateTime.now().minusDays(1));

        List<Model> models = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(models);

        // Act
        ModelDetailResponse[] result = modelListService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.length);

        assertEquals("ModelX", result[0].getName());
        assertEquals("ProviderA", result[0].getProvider());
        assertEquals(ModelType.CHAT, result[0].getType());
        assertEquals(true, result[0].isEnabled());
        assertNotNull(result[0].getCreatedAt());

        assertEquals("ModelY", result[1].getName());
        assertEquals("ProviderB", result[1].getProvider());
        assertEquals(ModelType.EMBEDDING, result[1].getType());
        assertEquals(false, result[1].isEnabled());
        assertNotNull(result[1].getCreatedAt());
    }

    @Test
    void execute_shouldReturnEmptyArrayWhenNoModels() {
        // Arrange
        when(modelRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        ModelDetailResponse[] result = modelListService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
