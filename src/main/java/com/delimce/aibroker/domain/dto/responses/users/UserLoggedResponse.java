package com.delimce.aibroker.domain.dto.responses.users;

public record UserLoggedResponse(
        String token,
        String name,
        String lastName,
        String email) {

}
