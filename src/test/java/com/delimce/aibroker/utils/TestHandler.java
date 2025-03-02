package com.delimce.aibroker.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.repositories.ProviderRepository;
import com.delimce.aibroker.domain.repositories.UserRepository;
import com.github.javafaker.Faker;

@SpringBootTest
public abstract class TestHandler {
    private static final Faker faker = new Faker();

    @Autowired
    protected ProviderRepository providerRepository;

    @Autowired
    protected ModelRepository modelRepository;

     @Autowired
    protected UserRepository userRepository;

    public static Faker faker() {
        return faker;
    }

    public void setUp() {
        purgeDatabase();
    }

    private void purgeDatabase() {
        // Example implementation, customize based on your actual database setup
        // This could use JPA repositories, JDBC template, or any database access method

        // For example, if using Spring Data JPA repositories:
        // userRepository.deleteAll();
        // orderRepository.deleteAll();

        // Or if using JDBC:
        // jdbcTemplate.execute("DELETE FROM orders");
        // jdbcTemplate.execute("DELETE FROM users");

        userRepository.deleteAll();
        providerRepository.deleteAll();
        modelRepository.deleteAll();
        System.out.println("Database purged before test execution");
    }

}
