package com.delimce.aibroker.infrastructure.controllers.account;

import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.application.account.AccountRegisterService;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
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

    @PostMapping("/auth")
    public ApiResponse login() {
        return responseOk("login ok");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserCreatedResponse userCreated = accountRegisterService.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseCreated(userCreated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError(e.getMessage()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseError("An error occurred during registration"));
        }
    }

}
