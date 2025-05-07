package com.delimce.aibroker.domain.ports;

public interface LoggerInterface {

    public void info(String message, Object... args);

    public void warn(String message);

    public void error(String message);

    public void error(String message, Object... args);

}
