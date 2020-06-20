package com.runemax.bot.api.interact;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.input.Mouse;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Interact {

    @Inject
    private static EventBus eventBus;

    public static final MenuDriver driver = new MenuDriver();
    public static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static final int MENU_REPLACE_DURATION = 150;

    public static void init() {
        Client.setInventoryDragDelay(20);
    }

    public static boolean isReady() {
        return !driver.isReplacing();
    }

    public static void interact(String target, String option, int identifier, int type, int param0, int param1) {
        Sleep.until(Interact::isReady, 1, MENU_REPLACE_DURATION + 10);

        MenuEntry menuEntry = new MenuEntry();
        menuEntry.setTarget(target);
        menuEntry.setOption(option);
        menuEntry.setIdentifier(identifier);
        menuEntry.setOpcode(type);
        menuEntry.setParam0(param0);
        menuEntry.setParam1(param1);
        menuEntry.setForceLeftClick(false);
        //System.out.println("interacting: " + menuEntry.toString());

        driver.startReplacing(new MenuEntry[]{menuEntry});

        executor.execute(() -> {
            try {
                Mouse.click(530, 280);
                Sleep.sleep(MENU_REPLACE_DURATION);
            } catch (Exception e) {
                log.info("weird", e);
            } finally {
                driver.stopReplacing();
            }
        });
    }
}
