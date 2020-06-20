package com.runemax.bot.api.exception;

import com.runemax.bot.api.exception.tags.FatalExceptionTag;

public class FatalBotException extends BotException implements FatalExceptionTag {
    public FatalBotException() {
        super();
    }

    public FatalBotException(String message) {
        super(message);
    }

    public FatalBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalBotException(Throwable cause) {
        super(cause);
    }

    protected FatalBotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
