package com.delimce.aibroker.infrastructure.controllers.users;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;

import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.application.users.UserListService;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserListService userListService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> listUsers(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader) {
        try {
            // Validate authorization header
            if (authorizationHeader == null || authorizationHeader.isEmpty()
                    || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(responseError("Valid Authorization header with Bearer token is required",
                                HttpStatus.UNAUTHORIZED.value()));
            }

            return ResponseEntity.ok(responseOk(userListService.execute()));
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }
}