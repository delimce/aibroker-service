package com.delimce.aibroker.domain.mappers.users;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserCreatedResponse userToUserCreatedResponse(User user);
}
