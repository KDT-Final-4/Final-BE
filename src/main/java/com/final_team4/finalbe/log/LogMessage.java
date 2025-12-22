package com.final_team4.finalbe.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LogMessage {

    private LogMessage() {}

    public static void info(LogStatus status, String action, String message, Object... args) {
        log.info("{}: {}", prepend(status, action, args), message);
    }

    public static void warn(LogStatus status, String action, String message, Object... args) {
        log.warn("{}: {}", prepend(status, action, args), message);
    }

    public static void error(LogStatus status, String action, Throwable e, String message, Object... args) {
        log.error("{}: {} {}", prepend(status, action, args), e, message);
    }

    private static Object[] prepend(LogStatus status, String action, Object[] args) {
        Object[] newArgs = new Object[args.length + 2];
        newArgs[0] = status.name();
        newArgs[1] = action;
        System.arraycopy(args, 0, newArgs, 2, args.length);
        return newArgs;
    }
}
