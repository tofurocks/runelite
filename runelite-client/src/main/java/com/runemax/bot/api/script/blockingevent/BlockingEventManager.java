package com.runemax.bot.api.script.blockingevent;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.script.blockingevent.events.LoginEvent;
import com.runemax.bot.api.script.blockingevent.events.WelcomeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BlockingEventManager {
    private final List<BlockingEvent> blockingEvents = new ArrayList<>(Arrays.asList(
        new LoginEvent(),
        new WelcomeEvent()
//            new ResizableEvent()
    ));

    @Nullable
    public BlockingEvent getBlocking() {
        for (BlockingEvent event : blockingEvents) {
            if (event.validate()) {
                return event;
            }
        }
        return null;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends BlockingEvent> T get(Class<T> clazz) {
        return (T) blockingEvents.stream().filter(in -> in.getClass().equals(clazz)).findFirst().orElseThrow(() -> new BotException("no event matched"));
    }

    public void remove(Predicate<? super BlockingEvent> predicate) {
        blockingEvents.removeIf(predicate);
    }
}
