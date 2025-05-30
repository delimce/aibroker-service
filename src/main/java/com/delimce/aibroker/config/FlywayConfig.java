package com.delimce.aibroker.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Configuration class for Flyway database migrations.
 */
@Configuration
public class FlywayConfig {

    @Autowired
    /**
     * Creates and configures the Flyway instance.
     * 
     * @param dataSource the application's main datasource
     * @return a fully configured Flyway instance
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();

        // Run the migration on startup
        flyway.migrate();

        return flyway;
    }
}
