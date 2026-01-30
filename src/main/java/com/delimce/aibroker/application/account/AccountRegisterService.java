package com.delimce.aibroker.application.account;

import com.delimce.aibroker.domain.dto.requests.users.UserRegistrationRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserCreatedResponse;
import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;
import com.delimce.aibroker.domain.exceptions.account.UserAlreadyExistsException;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throws UserAlreadyExistsException, IllegalArgumentException, SecurityValidationException {
        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new IllegalArgumentException(
                    "Password and password confirmation do not match");
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

        UserToken token = jwtTokenInterface.generateUserToken(user);
        user.setTempToken(token.token());
        user.setTokenTs(token.issuedAt());

        return userMapper.userToUserCreatedResponse(userRepository.save(user));
    }
}
