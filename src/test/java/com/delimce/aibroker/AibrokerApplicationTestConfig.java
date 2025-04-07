package com.delimce.aibroker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.delimce.aibroker.domain.ports.LoggerInterface;
import com.delimce.aibroker.infrastructure.adapters.Slf4jLoggerAdapter;

@Configuration
public class AibrokerApplicationTestConfig {
    
    @Bean
    @Primary
    public LoggerInterface primaryLoggerInterface() {
        return new Slf4jLoggerAdapter();
    }
}
