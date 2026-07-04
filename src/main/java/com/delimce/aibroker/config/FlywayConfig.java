package com.delimce.aibroker.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration class for Flyway database migrations.
 */
@Configuration
public class FlywayConfig {

    /**
     * Creates and configures the Flyway instance.
     * 
     * @param dataSource the application's main datasource
     * @return a fully configured Flyway instance
     */
    @Bean
    public Flyway flyway(
            DataSource dataSource,
            @Value("${spring.flyway.locations:classpath:db/migration}") String flywayLocations,
            @Value("${spring.flyway.mixed:false}") boolean mixed
    ) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayLocations)
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .mixed(mixed)
                .load();

        // Run the migration on startup
        flyway.migrate();

        return flyway;
    }
}
