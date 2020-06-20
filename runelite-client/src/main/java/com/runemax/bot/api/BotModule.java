package com.runemax.bot.api;

import com.google.inject.AbstractModule;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.interact.Interact;

public class BotModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(
                Client.class,
                OnGameThread.class,
                Interact.class
        );
    }
}
