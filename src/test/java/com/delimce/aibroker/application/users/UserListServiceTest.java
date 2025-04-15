package com.delimce.aibroker.application.users;

import com.delimce.aibroker.domain.dto.responses.users.UserListResponse;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.mappers.users.UserMapper;
import com.delimce.aibroker.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserListServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserListService userListService;

    private List<User> users;
    private List<UserListResponse> userResponses;

    @BeforeEach
    void setUp() {
        // Prepare test data
        users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        userResponses = new ArrayList<>();
        userResponses.add(new UserListResponse(null, null, null, null, null

        ));
        userResponses.add(new UserListResponse(null, null, null, null, null));
    }

    @Test
    void execute_ShouldReturnUserListResponses() {
        // Arrange
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserListResponse(any(User.class)))
                .thenReturn(userResponses.get(0), userResponses.get(1));

        // Act
        List<UserListResponse> result = userListService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userResponses, result);
    }

    @Test
    void execute_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<UserListResponse> result = userListService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}