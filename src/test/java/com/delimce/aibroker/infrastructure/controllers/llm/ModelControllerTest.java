package com.delimce.aibroker.infrastructure.controllers.llm;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delimce.aibroker.application.llm.ModelListService;
import com.delimce.aibroker.domain.dto.responses.llm.ModelDetailResponse;
import com.delimce.aibroker.domain.enums.ModelType;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ModelController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ModelControllerTest.SecurityTestConfig.class)
class ModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelListService modelListService;

    private static final DateTimeFormatter ISO_FORMATTER =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void listModels_whenServiceReturnsModels_returnsOkResponse()
        throws Exception {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 10, 9, 30);
        LocalDateTime createdAtSecond = createdAt.minusDays(2);

        ModelDetailResponse[] responses = new ModelDetailResponse[] {
            new ModelDetailResponse(
                "gpt-4o",
                "OpenAI",
                ModelType.CHAT,
                true,
                createdAt
            ),
            new ModelDetailResponse(
                "mistral-embed",
                "Mistral",
                ModelType.EMBEDDING,
                false,
                createdAtSecond
            ),
        };

        when(modelListService.execute()).thenReturn(responses);

        mockMvc
            .perform(get("/llm/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info").isArray())
            .andExpect(jsonPath("$.info.length()").value(2))
            .andExpect(jsonPath("$.info[0].name").value("gpt-4o"))
            .andExpect(jsonPath("$.info[0].provider").value("OpenAI"))
            .andExpect(jsonPath("$.info[0].type").value(ModelType.CHAT.name()))
            .andExpect(jsonPath("$.info[0].enabled").value(true))
            .andExpect(
                jsonPath("$.info[0].createdAt").value(
                    createdAt.format(ISO_FORMATTER)
                )
            )
            .andExpect(jsonPath("$.info[1].name").value("mistral-embed"))
            .andExpect(jsonPath("$.info[1].provider").value("Mistral"))
            .andExpect(
                jsonPath("$.info[1].type").value(ModelType.EMBEDDING.name())
            )
            .andExpect(jsonPath("$.info[1].enabled").value(false))
            .andExpect(
                jsonPath("$.info[1].createdAt").value(
                    createdAtSecond.format(ISO_FORMATTER)
                )
            );

        verify(modelListService).execute();
    }

    @Test
    void listModels_whenServiceReturnsEmptyArray_returnsOkWithNoItems()
        throws Exception {
        when(modelListService.execute()).thenReturn(new ModelDetailResponse[0]);

        mockMvc
            .perform(get("/llm/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.info").isArray())
            .andExpect(jsonPath("$.info.length()").value(0));

        verify(modelListService).execute();
    }

    @Test
    void listModels_whenServiceThrowsIllegalArgument_returnsBadRequest()
        throws Exception {
        when(modelListService.execute()).thenThrow(
            new IllegalArgumentException("Unsupported provider region")
        );

        mockMvc
            .perform(get("/llm/list"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(
                jsonPath("$.message").value("Unsupported provider region")
            )
            .andExpect(jsonPath("$.info").value(nullValue()));

        verify(modelListService).execute();
    }

    @Test
    void listModels_whenServiceThrowsUnhandledException_returnsInternalServerError()
        throws Exception {
        when(modelListService.execute()).thenThrow(
            new RuntimeException("Unexpected failure")
        );

        mockMvc
            .perform(get("/llm/list"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(
                jsonPath("$.message").value(
                    "An error occurred during execution"
                )
            )
            .andExpect(jsonPath("$.info").value(nullValue()));

        verify(modelListService).execute();
    }

    @TestConfiguration
    static class SecurityTestConfig {

        @Bean
        JwtTokenInterface jwtTokenInterface() {
            return mock(JwtTokenInterface.class);
        }

        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}
