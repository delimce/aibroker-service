package com.delimce.aibroker.application.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@Service
public class AccountVerifiedService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenInterface jwtTokenInterface;

    @Autowired
    private UserMapper userMapper;

    public UserMinDetail execute(String token) {
        String email = jwtTokenInterface.extractEmail(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        if (!jwtTokenInterface.isTokenValid(token, user)) {
            throw new IllegalArgumentException("Token is expired or invalid");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            user.setTempToken(null);
            user = userRepository.save(user);
        }

        return userMapper.userToUserMinDetail(user);
    }
}