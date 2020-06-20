package com.runemax.bot.api.movement;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.input.Mouse;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.inventory.InventoryItem;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.Locatable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@Slf4j
public class Movement {
    @Getter
    @Setter
    private static boolean autoToggleRunAndDrinkStamina = true;
    @Getter
    @Setter
    private static int runEnergyThreshold = 90;
    @Getter
    @Setter
    private static int stamEnergyThreshold = 15;

    public static void setDestination(int sceneX, int sceneY) {
        OnGameThread.invokeAndWait(() -> null);
        Sleep.sleep(25);
        Client.setSelectedSceneTileX(sceneX);
        Client.setSelectedSceneTileY(sceneY);
        Client.setViewportWalking(true);
    }

    public static boolean checkStamAndRun(){//returns true if interacted
        if(isRunEnabled()){
            if(getRunEnergy() <= stamEnergyThreshold){
                InventoryItem stam = Inventory.first(item -> item.getName().startsWith("Stamina potion(") && !item.isNoted());
                if (stam.isPresent()) {
                    stam.interact("Drink");
                    return true;
                }
            }

            return false;
        }

        if (isStaminaActive() || getRunEnergy() >= runEnergyThreshold) {
            toggleRun();
            return true;
        }

        InventoryItem stam = Inventory.first(item -> item.getName().startsWith("Stamina potion(") && !item.isNoted());
        if (stam.isPresent()) {
            stam.interact("Drink");
            return true;
        }

        return false;
    }

    public static boolean clickMinimap(LocalPoint localPoint) {
        Point point = Perspective.localToMinimap(Client.getInstance(), localPoint);
        if(point != null) {
            Mouse.click(point);
            return true;
        }
        return false;
    }

    public static void walk(Point scenePoint) {
        setDestination(scenePoint.getX(), scenePoint.getY());
        if (autoToggleRunAndDrinkStamina) {
            checkStamAndRun();
        }
    }

    public static boolean walk(int worldX, int worldY) {
        int sceneX = worldX - Client.getBaseX();
        int sceneY = worldY - Client.getBaseY();

        if (sceneX > 104) {
            sceneX = 104;
        }

        if (sceneX < 0) {
            sceneX = 0;
        }

        if (sceneY > 104) {
            sceneY = 104;
        }

        if (sceneY < 0) {
            sceneY = 0;
        }
//
//        WorldPoint worldPoint = WorldPoint.fromScene(Client.getRl(), sceneX, sceneY, Client.getPlane());

//        return clickMinimap(LocalPoint.fromWorld(Client.getRl(), worldX, worldY));
        walk(new Point(sceneX, sceneY));
        return true;
    }

    public static boolean walk(WorldPoint worldPoint) {
        return walk(worldPoint.getX(), worldPoint.getY());
    }

    public static void walk(Locatable locatable) {
        walk(locatable.getSceneLocation());
    }

    public static int getRunEnergy() {
        return Client.getEnergy();
    }

    public static boolean isRunEnabled() {
        return Client.getVarpValue(Client.getVarps(), 173) == 1;
    }

    public static void toggleRun() {
        Widgets.get(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB).interact("Toggle run");
    }

    public static int getStamina() {
        return Varps.getBit(Varbits.RUN_SLOWED_DEPLETION_ACTIVE.getId());
    }

    public static boolean isStaminaActive() {
        return getStamina() != 0;
    }
}
