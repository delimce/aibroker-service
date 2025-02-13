package com.delimce.shared.domain.contracts;

import com.delimce.shared.domain.dto.ApiResponse;

public interface ControllerInterface {

    public ApiResponse responseOk(Object data);

    public ApiResponse responseCreated(Object data);

}
