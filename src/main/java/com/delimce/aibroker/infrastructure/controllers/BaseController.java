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
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;

import io.jsonwebtoken.io.DecodingException;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
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
    public ApiResponse responseError(String message, int status) {
        return new ApiResponse(message, status);
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

    protected ResponseEntity<ApiResponse> badJwtConfigExceptionResponse(
            DecodingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseError("Invalid token jwt config", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    protected ResponseEntity<ApiResponse> illegalArgumentExceptionResponse(
            IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(responseError(e.getMessage()));
    }

    protected ResponseEntity<ApiResponse> unhandledExceptionResponse(
            Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseError("An error occurred during execution", 500));
    }

    protected ResponseEntity<ApiResponse> unAuthorizedExceptionResponse(
            SecurityValidationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(responseError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

}
