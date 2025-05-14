package com.delimce.aibroker.application.account;

import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.dto.responses.users.UserMinDetail;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@Service
public class AccountVerifiedService {

    private final UserRepository userRepository;
    private final JwtTokenInterface jwtTokenInterface;
    private final UserMapper userMapper;

    public AccountVerifiedService(UserRepository userRepository,
            JwtTokenInterface jwtTokenInterface,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtTokenInterface = jwtTokenInterface;
        this.userMapper = userMapper;
    }

    public UserMinDetail execute(String token) throws JwtTokenException {

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