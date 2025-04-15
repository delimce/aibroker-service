package com.delimce.aibroker.domain.dto.responses.users;

import java.time.LocalDateTime;

public record UserMinDetail(
        String email,
        LocalDateTime createdAt) {

}
