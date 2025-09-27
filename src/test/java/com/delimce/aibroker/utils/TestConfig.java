package com.delimce.aibroker.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.delimce.aibroker.config.JwtAuthenticationFilter;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.ports.LoggerInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;
import org.springframework.boot.actuate.health.HealthEndpoint;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return mock(JwtAuthenticationFilter.class);
    }

    @Bean
    public JwtTokenInterface jwtTokenInterface() {
        return mock(JwtTokenInterface.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return mock(UserDetailsService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public LoggerInterface loggerInterface() {
        return mock(LoggerInterface.class);
    }

    @Bean
    public HealthEndpoint healthEndpoint() {
        return mock(HealthEndpoint.class);
    }
}