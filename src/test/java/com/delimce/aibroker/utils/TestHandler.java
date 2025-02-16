package com.delimce.aibroker.utils;

import org.springframework.boot.test.context.SpringBootTest;
import com.github.javafaker.Faker;

@SpringBootTest
public abstract class TestHandler {
    private static final Faker faker = new Faker();

    public static Faker faker() {
        return faker;
    }

}
