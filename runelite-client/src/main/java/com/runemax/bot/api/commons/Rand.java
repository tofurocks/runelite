package com.runemax.bot.api.commons;

import java.util.Random;

public class Rand {
    private static final Random r = new Random();

    static {
        //todo make not retarded
        for (int i = 0; i < System.currentTimeMillis() % 100; i++) {
            r.nextInt();
        }
    }

    private Rand() {
    }

    /**
     * Returns the next pseudo-random double, distributed between min and max.
     *
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the random number between min and max
     */
    public static synchronized double nextDouble(final double min, final double max) {
        final double a = Math.min(min, max), b = Math.max(max, min);
        return a + r.nextDouble() * (b - a);
    }

    /**
     * Returns the next pseudo-random double.
     *
     * @return the next pseudo-random, a value between {@code 0.0} and {@code 1.0}.
     */
    public static synchronized double nextDouble() {
        return r.nextDouble();
    }

    public static synchronized int nextDuration(float secondsLow, float secondsHigh) {
        return nextInt((int) (secondsLow * 1000), (int) (secondsHigh * 1000));
    }

    public static synchronized int nextInt(int lowerBoundInclusive, int upperBoundExclusive) {
        return lowerBoundInclusive + r.nextInt(upperBoundExclusive - lowerBoundInclusive);
    }

    public static synchronized int nextInt() {
        return r.nextInt();
    }

    public static synchronized boolean nextBool() {
        return r.nextBoolean();
    }

    public static int gaussian(int lowerBoundInclusive, int upperBoundInclusive, int mean, int stdev) {
        int result;
        double val;
        Random rand = new Random();
        do {
            val = rand.nextGaussian() * stdev + mean;
            result = (int) Math.round(val);
        } while (result > upperBoundInclusive || result < lowerBoundInclusive);
        return result;
    }
}
