package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

@RestController
final class HealthController extends BaseController {

    @GetMapping("/health")
    public ApiResponse health() {
        return responseOk(null);
    }

}
