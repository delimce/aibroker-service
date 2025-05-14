package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.ports.LoggerInterface;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

@RestController
final class HealthController extends BaseController {

    public HealthController(LoggerInterface logger) {
        super(logger);
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return responseOk(null);
    }

}
