package com.delimce.aibroker;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AibrokerApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
        String activeProfile = System.getProperty(
            "spring.profiles.active",
            System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "cloud")
        );
        boolean homeProfile = isHomeProfile(activeProfile);
        dotenv.entries().forEach(entry -> {
            if (
                homeProfile && entry.getKey().startsWith("SPRING.DATASOURCE.")
            ) {
                return;
            }

            System.setProperty(entry.getKey(), entry.getValue());
        });
        if (homeProfile) {
            createHomeDatabaseDirectory();
        }

        SpringApplication.run(AibrokerApplication.class, args);
    }

    private static boolean isHomeProfile(String activeProfile) {
        return (
            activeProfile != null &&
            activeProfile.matches("(^|.*,\\s*)home(\\s*,.*|$)")
        );
    }

    private static void createHomeDatabaseDirectory() {
        try {
            Files.createDirectories(Path.of("home-db"));
        } catch (IOException exception) {
            throw new IllegalStateException(
                "Could not create home database directory",
                exception
            );
        }
    }
}
