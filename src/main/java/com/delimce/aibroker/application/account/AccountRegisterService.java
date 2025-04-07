package com.delimce.aibroker.application.account;

import org.springframework.beans.factory.annotation.Autowired;

import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;

@Service
public class AccountRegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public UserCreatedResponse execute(UserRegistrationRequest request)
            throws UserAlreadyExistsException,
            IllegalArgumentException {

        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new IllegalArgumentException("Password and password confirmation do not match");
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            throw new UserAlreadyExistsException(request.getEmail());
        }
        user = User.builder()
                .name(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return userMapper.userToUserCreatedResponse(userRepository.save(user));
    }
}
