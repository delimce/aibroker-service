package com.delimce.aibroker.application.users;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.repositories.UserRepository;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.dto.responses.users.UserListResponse;

@Service
public class UserListService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserListService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserListResponse> execute() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userToUserListResponse)
                .collect(Collectors.toList());
    }
}