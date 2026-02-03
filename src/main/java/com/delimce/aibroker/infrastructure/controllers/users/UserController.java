package com.delimce.aibroker.infrastructure.controllers.users;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;
import com.delimce.aibroker.application.users.UserListService;
import com.delimce.aibroker.application.users.UserRefreshService;
import com.delimce.aibroker.infrastructure.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    private final UserListService userListService;
    private final UserRefreshService userRefreshService;

    public UserController(UserListService userListService, UserRefreshService userRefreshService) {
        this.userListService = userListService;
        this.userRefreshService = userRefreshService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> listUsers() {
        try {
            return ResponseEntity.ok(responseOk(userListService.execute()));
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }

    @PutMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshUserData() {
        try {
            return ResponseEntity.ok(responseOk(userRefreshService.execute()));
        } catch (SecurityValidationException e) {
            return unAuthorizedExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }
}
