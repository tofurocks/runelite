package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.interfaces.RequiresEquipment;
import com.runemax.bot.interfaces.RequiresItems;
import com.runemax.bot.interfaces.RequiresStartLocation;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;

public class SplashTask extends Task implements RequiresStartLocation, RequiresItems, RequiresEquipment {

    public SplashTask(String taskDescription) {
        super(taskDescription);
    }

    @Override
    public Result execute(TaskSet parentQueue) {
        return Result.FAILED;
    }

    @Override
    public WorldPoint getStartLocation() {
        return null;
    }

    @Override
    public HashMap<Integer, Integer> getItemsRequired() {
        return new HashMap<>() {{
            put(ItemID.AIR_RUNE, 1000);
            put(ItemID.MIND_RUNE, 1000);
        }};
    }

    @Override
    public HashMap<EquipmentInventorySlot, Integer> getEquipmentRequired() {
        return new HashMap<>() {{
            put(EquipmentInventorySlot.WEAPON, ItemID.CURSED_GOBLIN_STAFF);
            put(EquipmentInventorySlot.HEAD, ItemID.IRON_FULL_HELM);
            put(EquipmentInventorySlot.BODY, ItemID.IRON_PLATEBODY);
            put(EquipmentInventorySlot.LEGS, ItemID.IRON_PLATELEGS);
        }};
    }

}
