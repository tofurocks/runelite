package com.runemax.bot.scripts.muling;

import lombok.Getter;
import lombok.Setter;

public class RestockOffer {
    @Getter
    @Setter
    String handle;
    @Getter
    @Setter
    int world;

    @Override
    public String toString() {
        return handle + " at " + world;
    }
}
