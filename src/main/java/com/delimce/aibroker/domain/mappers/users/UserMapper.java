package com.delimce.aibroker.domain.mappers.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.entities.User;

import com.delimce.aibroker.domain.dto.responses.users.UserListResponse;
import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "tempToken", target = "token")
    UserCreatedResponse userToUserCreatedResponse(User user);

    UserListResponse userToUserListResponse(User user);

    UserMinDetail userToUserMinDetail(User user);
}
