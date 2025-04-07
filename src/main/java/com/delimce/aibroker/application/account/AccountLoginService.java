package com.delimce.aibroker.application.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.delimce.aibroker.domain.dto.requests.users.UserLoginRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.account.UserIsNotActiveException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@Service
public class AccountLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenInterface jwtTokenAdapter;

    public UserLoggedResponse execute(UserLoginRequest request) throws UserIsNotActiveException {
        var user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserIsNotActiveException();
        }

        String token = jwtTokenAdapter.generateToken(user);
        return new UserLoggedResponse(
                token,
                user.getName(),
                user.getLastName(),
                user.getEmail());

    }

}
