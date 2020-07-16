package com.jd.platform.hotkey.client.log;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-04-21
 */
public interface HotKeyLogger {

    void debug(Class<?> loggerClass, String info);

    void info(Class<?> loggerClass, String info);

    void warn(Class<?> loggerClass, String info);

    void error(Class<?> className, String info);

    void error(Class<?> loggerClass, String message, Throwable cause);
}
