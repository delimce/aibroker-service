package com.delimce.aibroker.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.delimce.aibroker.domain.repositories.ModelRepository;
import com.delimce.aibroker.domain.repositories.ProviderRepository;
import com.delimce.aibroker.domain.repositories.UserRepository;
import com.github.javafaker.Faker;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestHandler {
    private static final Faker faker = new Faker();

    @Autowired(required = false)
    protected ProviderRepository providerRepository;

    @Autowired(required = false)
    protected ModelRepository modelRepository;

    @Autowired(required = false)
    protected UserRepository userRepository;

    public static Faker faker() {
        return faker;
    }

    public void setUp() {
        purgeDatabase();
    }

    private void purgeDatabase() {
        userRepository.deleteAll();
        providerRepository.deleteAll();
        modelRepository.deleteAll();
        System.out.println("Database purged before test execution");
    }
}
