package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@AllArgsConstructor
final class HealthController extends BaseController {

    private final HealthEndpoint healthEndpoint;
    private static final String ERROR_MESSAGE = "Service Unavailable";

    @GetMapping("/health")
    public ApiResponse health() {
        // Use actuator HealthEndpoint for aggregated health details
        try {
            return responseOk(healthEndpoint.health());
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return responseError(ERROR_MESSAGE, 500);
        }

    }

}
