package com.runemax.bot.api.exception;

public class RickkPointerException extends BotException {
    public RickkPointerException() {
        super();
    }

    public RickkPointerException(String message) {
        super(message);
    }

    public RickkPointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RickkPointerException(Throwable cause) {
        super(cause);
    }

    protected RickkPointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
