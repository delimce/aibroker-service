package com.delimce.aibroker.infrastructure.controllers.account;

import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.application.account.AccountLoginService;
import com.delimce.aibroker.application.account.AccountRegisterService;
import com.delimce.aibroker.application.account.AccountVerifiedService;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.dto.requests.users.UserLoginRequest;
import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.exceptions.account.UserIsNotActiveException;
import com.delimce.aibroker.infrastructure.controllers.BaseController;
import com.delimce.aibroker.domain.ports.LoggerInterface;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountRegisterService accountRegisterService;
    private final AccountLoginService accountLoginService;
    private final AccountVerifiedService accountVerifiedService;

    public AccountController(AccountRegisterService accountRegisterService,
            AccountLoginService accountLoginService,
            AccountVerifiedService accountVerifiedService,
            LoggerInterface logger) {
        super(logger);
        this.accountRegisterService = accountRegisterService;
        this.accountLoginService = accountLoginService;
        this.accountVerifiedService = accountVerifiedService;
    }

    @PostMapping("/auth")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            UserLoggedResponse response = accountLoginService.execute(request);
            return ResponseEntity.ok(responseOk(response));
        } catch (UserIsNotActiveException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(responseError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (IllegalArgumentException e) {
            return illegalArgumentExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserCreatedResponse userCreated = accountRegisterService.execute(request);
            return ResponseEntity.ok(responseCreated(userCreated));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(responseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return illegalArgumentExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse> verify(@PathVariable String token) {
        try {
            UserMinDetail response = accountVerifiedService.execute(token);
            return ResponseEntity.ok(responseOk(response));
        } catch (IllegalArgumentException e) {
            return illegalArgumentExceptionResponse(e);
        } catch (SecurityValidationException e) {
            return unAuthorizedExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }
}
