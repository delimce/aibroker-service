package com.delimce.aibroker.domain.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;

import com.delimce.aibroker.utils.TestHandler;

import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;

public class UserRepositoryTest extends TestHandler {

    @BeforeEach
    void setup() {
        super.setUp();
    }

    @Test
    void repositoryInjectionTest() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void saveAndFindUserTest() {
        // Create a new User instance. Adjust the field setters to match your User
        // entity properties.
        var user = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .password(faker().internet().password())
                .email(faker().internet().emailAddress())
                .build();

        // Save the user and assert that it gets an ID.
        var savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Retrieve the user by ID and verify its properties.
        var foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(savedUser.getName());
        // assert createdAt and updatedAt fields
        assertThat(foundUser.get().getCreatedAt()).isNotNull();
        assertThat(foundUser.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void saveUserWithExistingEmailTest() {
        // Create a new User instance. Adjust the field setters to match your User
        // entity properties.
        var user = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .password(faker().internet().password())
                .email(faker().internet().emailAddress())
                .status(UserStatus.INACTIVE)
                .build();

        // Save the user and assert that it gets an ID.
        var savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Create a new User instance with the same email as the previous user.
        User userWithExistingEmail = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .password(faker().internet().password())
                .email(savedUser.getEmail())
                .build();

        // Attempt to save the user with the existing email.
        var isFailed = false;
        try {
            userRepository.save(userWithExistingEmail);
        } catch (Exception e) {
            isFailed = true;
            assertThat(e).isInstanceOf(DataIntegrityViolationException.class);
        }
        assertThat(isFailed).isTrue();
    }

    @Test
    void findByEmailTest() {
        // Create a new User instance
        var user = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .password(faker().internet().password())
                .email(faker().internet().emailAddress())
                .build();

        // Save the user
        userRepository.save(user);

        // Find the user by email
        var foundUser = userRepository.findByEmail(user.getEmail());

        // Verify that the user was found
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    void findByEmailNotFoundTest() {
        // Try to find a user with a non-existent email
        var nonExistentEmail = "nonexistent" + System.currentTimeMillis() + "@example.com";
        var foundUser = userRepository.findByEmail(nonExistentEmail);

        // Verify that no user was found
        assertThat(foundUser).isNull();
    }
}