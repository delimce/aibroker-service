package com.delimce.aibroker.infrastructure.controllers;

import com.delimce.aibroker.domain.dto.ApiResponse;

public class BaseController implements ControllerInterface {

    @Override
    public ApiResponse responseOk(Object data) {
        return new ApiResponse(data);
    }

    @Override
    public ApiResponse responseCreated(Object data) {
        return new ApiResponse(data, 201, ApiResponse.CREATED);
    }

}
