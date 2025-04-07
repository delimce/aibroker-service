package com.delimce.aibroker.domain.dto.responses.users;

import java.time.LocalDateTime;

public record UserCreatedResponse(
        String name,
        String lastName,
        String email,
        LocalDateTime createdAt) {
}
