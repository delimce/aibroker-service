package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.ports.LoggerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLoggerAdapter implements LoggerInterface {

    private final Logger logger;

    public Slf4jLoggerAdapter() {
        // Default constructor uses the class name as logger name
        this.logger = LoggerFactory.getLogger(Slf4jLoggerAdapter.class);
    }

    public Slf4jLoggerAdapter(Class<?> clazz) {
        // Constructor that allows specifying the class for logging
        this.logger = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }
}