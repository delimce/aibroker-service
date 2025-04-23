package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.ports.ApiClientInterface;
import com.delimce.aibroker.domain.ports.LoggerInterface;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientAdapter implements ApiClientInterface {

    private final LoggerInterface logger;
    private final WebClient webClient;
    private static final String PING_URL = "https://httpbin.org/get";

    public WebClientAdapter(WebClient.Builder webClientBuilder, LoggerInterface logger) {
        this.webClient = webClientBuilder.build();
        this.logger = logger;
    }

    /**
     * Pings a public URL using WebClient to check for basic connectivity.
     *
     * @return true if the ping receives a 2xx success status, false otherwise.
     */
    @Override
    public boolean ping() {
        try {
            // Perform a GET request to the PING_URL
            // blockOptional() is used to synchronously get the result,
            // matching the interface signature. Returns false on errors or empty mono.
            return webClient.get()
                    .uri(PING_URL)
                    .retrieve() // Throws WebClientResponseException for non-2xx/3xx status codes by default
                    .toBodilessEntity() // We only need the status code
                    .flatMap(response -> Mono.just(response.getStatusCode().is2xxSuccessful()))
                    .onErrorResume(e -> {
                        // Log errors using LoggerInterface
                        logger.warn(String.format("Ping to %s failed: %s", PING_URL, e.getMessage()));
                        return Mono.just(false); // Indicate failure
                    })
                    .blockOptional() // Block synchronously for the result
                    .orElse(false); // Default to false if mono completes empty or times out
        } catch (Exception e) {
            // Catch synchronous exceptions during the blocking call
            logger.error("Unexpected error during ping to {}: {}", PING_URL, e.getMessage(), e); // Use LoggerInterface
            return false;
        }
    }
}
