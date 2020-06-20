package com.runemax.bot.api.commons;

import java.util.function.BooleanSupplier;
import lombok.Getter;
import lombok.Setter;
import com.runemax.bot.api.exception.BotException;

public class Sleep {
    @Setter
    @Getter
    private static int SLEEP_UNTIL_POLL_RATE = 100;

    public static class TimeoutException extends BotException{}

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(int min, int max){
        sleep(Rand.nextInt(min, max));
    }

    public static void until(BooleanSupplier condition, int pollRate, long timeout) {
        long start = System.currentTimeMillis();
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > start + timeout) {
                throw new TimeoutException();
            }

            sleep(pollRate);
        }
    }

    public static void until(BooleanSupplier condition, long timeout) {
        until(condition, SLEEP_UNTIL_POLL_RATE, timeout);
    }

    public static boolean untilWithConfirm(BooleanSupplier condition, int pollRate, long timeout){
        try {
            until(condition, pollRate, timeout);
            return true;
        }catch (TimeoutException e){
            return false;
        }
    }

    public static boolean untilWithConfirm(BooleanSupplier condition, long timeout){
        return untilWithConfirm(condition, SLEEP_UNTIL_POLL_RATE, timeout);
    }
}
