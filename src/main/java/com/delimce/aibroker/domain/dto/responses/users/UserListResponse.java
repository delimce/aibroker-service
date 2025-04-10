package com.delimce.aibroker.domain.dto.responses.users;

import com.delimce.aibroker.domain.enums.UserStatus;

public record UserListResponse(
                Long id,
                String name,
                String lastName,
                String email,
                UserStatus status) {
}