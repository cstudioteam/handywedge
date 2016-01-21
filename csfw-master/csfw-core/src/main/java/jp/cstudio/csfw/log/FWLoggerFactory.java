package jp.cstudio.csfw.log;

import org.slf4j.LoggerFactory;

import jp.cstudio.csfw.log.FWLogger;

public final class FWLoggerFactory {

    private FWLoggerFactory() {
    }

    public static FWLogger getLogger(Class<?> clazz) {

        return new FWLoggerImpl(LoggerFactory.getLogger(clazz));
    }
}
