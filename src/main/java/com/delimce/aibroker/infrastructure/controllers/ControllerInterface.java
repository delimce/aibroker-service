package com.delimce.aibroker.infrastructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.delimce.aibroker.domain.dto.ApiResponse;

public interface ControllerInterface {

    public ApiResponse responseOk(Object data);

    public ApiResponse responseCreated(Object data);

    public ApiResponse responseError(String message);

    public ApiResponse responseError(String message, int status);

    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex);

}
