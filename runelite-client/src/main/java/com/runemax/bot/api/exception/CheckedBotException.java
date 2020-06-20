package com.runemax.bot.api.exception;

public class CheckedBotException extends Exception{
    public CheckedBotException() {
        super();
    }

    public CheckedBotException(String message) {
        super(message);
    }

    public CheckedBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckedBotException(Throwable cause) {
        super(cause);
    }

    protected CheckedBotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
