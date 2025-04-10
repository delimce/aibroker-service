package com.delimce.aibroker.infrastructure.controllers.account;

import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.application.account.AccountLoginService;
import com.delimce.aibroker.application.account.AccountRegisterService;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.dto.requests.users.UserLoginRequest;
import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.exceptions.account.UserIsNotActiveException;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Autowired
    private AccountRegisterService accountRegisterService;

    @Autowired
    private AccountLoginService accountLoginService;

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

}
