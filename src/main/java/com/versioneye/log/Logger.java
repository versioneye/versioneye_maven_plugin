package com.versioneye.log;

import static com.versioneye.log.Logger.LogLevel.INFO;

public abstract class Logger {

    private static Logger logger = null;

    public static Logger getLogger() {
        if(Logger.logger == null) {
            Logger.logger = new StandardOutLogger(INFO);
        }
        return logger;
    }

    static void setLogger(Logger logger) {
        Logger.logger = logger;
    }

    abstract void setLogLevel(LogLevel level);

    public abstract void debug(String message);
    public abstract void debug(String message, Throwable t);

    public abstract void info(String message);
    public abstract void info(String message, Throwable t);

    public abstract void warn(String message);
    public abstract void warn(String message, Throwable t);

    public abstract void error(String message);
    public abstract void error(String message, Throwable t);

    public enum LogLevel {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        NONE(4);

        private int level;

        LogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
