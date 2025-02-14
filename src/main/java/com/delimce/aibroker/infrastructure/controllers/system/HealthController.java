package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.shared.domain.dto.ApiResponse;
import com.delimce.shared.infrastructure.BaseController;

@RestController
final class HealthController extends BaseController {

    @GetMapping("/health")
    public ApiResponse health() {
        return responseOk(null);
    }

}
