package com.delimce.aibroker.application.account;

import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;

@Service
public class AccountRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenInterface jwtTokenInterface;

    public AccountRegisterService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  UserMapper userMapper,
                                  JwtTokenInterface jwtTokenInterface) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtTokenInterface = jwtTokenInterface;
    }

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

        String token = jwtTokenInterface.generateToken(user);
        user.setTempToken(token);

        return userMapper.userToUserCreatedResponse(userRepository.save(user));
    }
}
