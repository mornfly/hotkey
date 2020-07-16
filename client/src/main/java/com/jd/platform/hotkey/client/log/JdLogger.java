package com.jd.platform.hotkey.client.log;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class JdLogger {

    private static HotKeyLogger logger = new DefaultLogger();

    public static void setLogger(HotKeyLogger log) {
        if (log != null) {
            logger = log;
        }
    }

    public static void debug(Class<?> loggerClass, String info) {
        logger.debug(loggerClass, info);
    }

    public static void info(Class<?> loggerClass, String info) {
        logger.info(loggerClass, info);
    }

    public static void warn(Class<?> loggerClass, String info) {
        logger.warn(loggerClass, info);
    }

    public static void error(Class<?> loggerClass, String info) {
        logger.error(loggerClass, info);
    }

    public static void error(Class<?> loggerClass, String message, Throwable cause) {
        logger.error(loggerClass, message, cause);
    }
}
