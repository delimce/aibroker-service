package com.delimce.aibroker.infrastructure.adapters;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;


class Slf4jLoggerAdapterTest {

    private ListAppender<ILoggingEvent> listAppender;
    private Slf4jLoggerAdapter loggerAdapter;
    private Slf4jLoggerAdapter customLoggerAdapter;

    @BeforeEach
    void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();

        // Configure and get the default logger
        Logger defaultLogger = (Logger) LoggerFactory.getLogger(Slf4jLoggerAdapter.class);
        defaultLogger.addAppender(listAppender);
        loggerAdapter = new Slf4jLoggerAdapter();

        // Configure and get a custom logger
        Logger customLogger = (Logger) LoggerFactory.getLogger(Slf4jLoggerAdapterTest.class);
        customLogger.addAppender(listAppender);
        customLoggerAdapter = new Slf4jLoggerAdapter(Slf4jLoggerAdapterTest.class);
        
        // Clear any existing log events
        listAppender.list.clear();
    }

    @Test
    void infoMethod_shouldLogWithInfoLevel() {
        // Given
        String message = "Info test message";

        // When
        loggerAdapter.info(message);

        // Then
        ILoggingEvent loggingEvent = listAppender.list.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());
        assertEquals(message, loggingEvent.getMessage());
        assertEquals(Slf4jLoggerAdapter.class.getName(), loggingEvent.getLoggerName());
    }

    @Test
    void warnMethod_shouldLogWithWarnLevel() {
        // Given
        String message = "Warn test message";

        // When
        loggerAdapter.warn(message);

        // Then
        ILoggingEvent loggingEvent = listAppender.list.get(0);
        assertEquals(Level.WARN, loggingEvent.getLevel());
        assertEquals(message, loggingEvent.getMessage());
    }

    @Test
    void errorMethod_shouldLogWithErrorLevel() {
        // Given
        String message = "Error test message";

        // When
        loggerAdapter.error(message);

        // Then
        ILoggingEvent loggingEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(message, loggingEvent.getMessage());
    }

    @Test
    void customClassConstructor_shouldUseSpecifiedClass() {
        // Given
        String message = "Custom logger test message";

        // When
        customLoggerAdapter.info(message);

        // Then
        ILoggingEvent loggingEvent = listAppender.list.get(0);
        assertEquals(Slf4jLoggerAdapterTest.class.getName(), loggingEvent.getLoggerName());
        assertEquals(message, loggingEvent.getMessage());
    }
}