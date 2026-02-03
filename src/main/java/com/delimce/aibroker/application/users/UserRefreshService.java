package com.delimce.aibroker.application.users;

import org.springframework.stereotype.Service;

import com.delimce.aibroker.application.BaseService;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@Service
public class UserRefreshService extends BaseService {

    private final JwtTokenInterface jwtTokenInterface;
    private final UserRepository userRepository;

    public UserRefreshService(JwtTokenInterface jwtTokenInterface, UserRepository userRepository) {
        this.jwtTokenInterface = jwtTokenInterface;
        this.userRepository = userRepository;
    }

    public UserLoggedResponse execute() throws JwtTokenException {

        User user = fetchAuthenticatedUser();
        UserToken token = jwtTokenInterface.generateUserToken(user);

        user.setTokenTs(token.issuedAt());
        userRepository.save(user);

        return new UserLoggedResponse(
                token.token(),
                user.getName(),
                user.getLastName(),
                user.getEmail());

    }

}
