package com.versioneye.log;

import org.apache.maven.plugin.logging.Log;

public class MavenLogger extends Logger {

    private Log log;

    public MavenLogger(Log log) {
        this.log = log;
        setLogger(this);
    }

    public void setLogLevel(Logger.LogLevel level) {
        throw new IllegalStateException("Not implemented");
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void debug(String message, Throwable t) {
        log.debug(message, t);
    }

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Throwable t) {
        log.info(message, t);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String message, Throwable t) {
        log.warn(message, t);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable t) {
        log.error(message, t);
    }
}
