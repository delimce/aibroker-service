package com.delimce.aibroker.infrastructure.controllers;

import com.delimce.aibroker.domain.dto.ApiResponse;

public interface ControllerInterface {

    public ApiResponse responseOk(Object data);

    public ApiResponse responseCreated(Object data);

}
