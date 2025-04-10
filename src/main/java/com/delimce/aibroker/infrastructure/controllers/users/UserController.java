package com.delimce.aibroker.infrastructure.controllers.users;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;

import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.application.users.UserListService;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserListService userListService;

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> listUsers() {
        try {
            return ResponseEntity.ok(responseOk(userListService.execute()));
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }
}