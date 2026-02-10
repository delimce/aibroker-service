package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.ports.AiApiClientInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@Primary
public class WebClientAdapter implements AiApiClientInterface {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public WebClientAdapter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a request to an LLM API endpoint based on the provided model's
     * provider.
     *
     * @param model        The Model entity containing provider details (URL, API
     *                     key).
     * @param modelRequest The request object containing model details and messages.
     * @return The parsed ModelChatResponse object, or null in case of error or
     *         non-successful response.
     */
    @SuppressWarnings("null")
    @Override
    public ModelChatResponse requestToModel(Model model, ModelRequest modelRequest) {

        if (model == null || model.getProvider() == null) {
            log.error("Cannot request model: Model or its Provider is null.");
            return null;
        }

        String targetUrl = model.getProvider().getBaseUrl();
        String apiKey = model.getProvider().getApiKey();

        // Basic validation for URL and API Key
        if (targetUrl == null || targetUrl.isBlank() || apiKey == null || apiKey.isBlank()) {
            log.error("Cannot request model '{}': Provider URL or API Key is missing.", model.getName());
            return null;
        }

        try {
            String responseBodyString = webClient.post()
                    .uri(targetUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(modelRequest), ModelRequest.class)
                    .retrieve() // Initiate the request and retrieve the response spec
                    .bodyToMono(String.class) // Convert the response body to a Mono<String>
                    .doOnSuccess(responseBody -> log.info(
                            "Successfully received response string from model {} at {}: {}",
                            modelRequest.getModel(), targetUrl, responseBody))
                    // Apply error handling *after* bodyToMono
                    .onErrorResume(WebClientResponseException.class, e -> {
                        HttpStatusCode statusCode = e.getStatusCode();
                        String responseBody = e.getResponseBodyAsString();
                        log.error(
                                "Request to model {} at {} failed with status {}: {}. Response body: {}",
                                modelRequest.getModel(), targetUrl, statusCode, e.getMessage(), responseBody, e);
                        return Mono.empty(); // Return empty Mono on client/server error
                    })
                    .onErrorResume(e -> !(e instanceof WebClientResponseException), e -> {
                        log.error("Request to model {} at {} failed due to unexpected error: {}",
                                modelRequest.getModel(), targetUrl, e.getMessage(), e);
                        return Mono.empty(); // Return empty Mono on other errors
                    })
                    .block(); // Block synchronously for the result

            if (responseBodyString == null) {
                return null; // Indicate failure
            }

            try {
                // Parse the JSON string into ModelChatResponse
                ModelChatResponse chatResponse = objectMapper.readValue(responseBodyString,
                        ModelChatResponse.class);

                return chatResponse;
            } catch (JsonProcessingException e) {
                log.error("Failed to parse JSON response from model {} at {}: {}. Response body: {}",
                        modelRequest.getModel(), targetUrl, e.getMessage(), responseBodyString, e);
                return null; // Indicate parsing failure
            }

        } catch (Exception e) {
            log.error("Unexpected synchronous error during requestToModel for model {}: {}",
                    modelRequest.getModel(), e.getMessage(), e);
            return null; // Indicate failure
        }
    }
}
