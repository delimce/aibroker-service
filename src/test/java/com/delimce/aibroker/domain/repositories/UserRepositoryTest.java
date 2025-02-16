package com.delimce.aibroker.domain.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.delimce.aibroker.utils.TestHandler;

import com.delimce.aibroker.domain.entities.User;

public class UserRepositoryTest extends TestHandler {

    @Autowired
    private UserRepository userRepository;

    @Test
    void repositoryInjectionTest() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void saveAndFindUserTest() {
        // Create a new User instance. Adjust the field setters to match your User
        // entity properties.
        User user = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .email(faker().internet().emailAddress())
                .build();

        // Save the user and assert that it gets an ID.
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Retrieve the user by ID and verify its properties.
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
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
        User user = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .email(faker().internet().emailAddress())
                .build();

        // Save the user and assert that it gets an ID.
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Create a new User instance with the same email as the previous user.
        User userWithExistingEmail = User.builder()
                .name(faker().name().firstName())
                .lastName(faker().name().lastName())
                .email(savedUser.getEmail())
                .build();

        // Attempt to save the user with the existing email.
        Boolean isFailed = false;
        try {
            userRepository.save(userWithExistingEmail);
        } catch (Exception e) {
            isFailed = true;
            assertThat(e).isInstanceOf(DataIntegrityViolationException.class);
        }
        assertThat(isFailed).isTrue();
    }

}
