package com.jd.platform.hotkey.client.log;

import org.slf4j.LoggerFactory;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-04-21
 */
public class DefaultLogger implements HotKeyLogger {

    @Override
    public void debug(Class<?> loggerClass, String info) {
        LoggerFactory.getLogger(loggerClass).debug(info);
    }

    @Override
    public void info(Class<?> loggerClass, String info) {
        LoggerFactory.getLogger(loggerClass).info(info);
    }

    @Override
    public void warn(Class<?> loggerClass, String info) {
        LoggerFactory.getLogger(loggerClass).warn(info);
    }

    @Override
    public void error(Class<?> loggerClass, String info) {
        LoggerFactory.getLogger(loggerClass).error(info);
    }

    @Override
    public void error(Class<?> loggerClass, String message, Throwable cause) {
        LoggerFactory.getLogger(loggerClass).error(message, cause);
    }
}
