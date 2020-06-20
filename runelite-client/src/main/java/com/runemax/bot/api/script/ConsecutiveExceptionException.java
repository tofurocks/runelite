package com.runemax.bot.api.script;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.exception.tags.FatalExceptionTag;

public class ConsecutiveExceptionException extends BotException implements FatalExceptionTag {
    public ConsecutiveExceptionException(Throwable cause) {
        super(cause);
    }
}
