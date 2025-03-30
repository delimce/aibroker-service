package com.delimce.aibroker.infrastructure.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.delimce.aibroker.domain.dto.ApiResponse;

@ControllerAdvice
public class BaseController extends ResponseEntityExceptionHandler
        implements ControllerInterface {

    @Override
    public ApiResponse responseOk(Object data) {
        return new ApiResponse(data);
    }

    @Override
    public ApiResponse responseCreated(Object data) {
        return new ApiResponse(data, 201, ApiResponse.CREATED);
    }

    @Override
    public ApiResponse responseError(String message) {
        return new ApiResponse(message, 400);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull org.springframework.http.HttpHeaders headers,
            @NonNull org.springframework.http.HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleMethodArgumentNotValid'");
    }

}
