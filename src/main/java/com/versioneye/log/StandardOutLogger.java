package com.versioneye.log;


import static com.versioneye.log.Logger.LogLevel.*;

public class StandardOutLogger extends Logger {
    protected static LogLevel level = INFO;

    public StandardOutLogger() {}

    public StandardOutLogger(LogLevel level) {
        StandardOutLogger.level = level;
    }

    public void setLogLevel(LogLevel level) {
        StandardOutLogger.level = level;
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable t) {
        logMessage(DEBUG, message, t);
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable t) {
        logMessage(INFO, message, t);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Throwable t) {
        logMessage(WARN, message, t);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable t) {
        logMessage(ERROR, message, t);
    }

    protected void logMessage(LogLevel level, String message, Throwable t) {
        if (level.getLevel() >= StandardOutLogger.level.getLevel())
            System.out.println("[" + level + "] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
}
