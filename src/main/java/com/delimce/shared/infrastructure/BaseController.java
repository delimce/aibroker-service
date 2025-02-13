package com.delimce.shared.infrastructure;

import com.delimce.shared.domain.contracts.ControllerInterface;
import com.delimce.shared.domain.dto.ApiResponse;

public class BaseController implements ControllerInterface {

    @Override
    public ApiResponse responseOk(Object data) {
        return new ApiResponse(data);
    }

    @Override
    public ApiResponse responseCreated(Object data) {
        return new ApiResponse(data, 201, "Created");
    }

}
