package com.runemax.bot.api.wrappers;

import com.runemax.bot.api.exception.RickkPointerException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RlWrapper<T> {
    private final T rl;

    public RlWrapper(@Nullable T rl) {
        this.rl = rl;
    }

    @Nonnull
    public T getRl() {
        if(rl == null) throw new RickkPointerException();
        return rl;
    }

    public boolean isEmpty(){
        return rl == null;
    }

    public boolean isPresent(){
        return rl != null;
    }
}
