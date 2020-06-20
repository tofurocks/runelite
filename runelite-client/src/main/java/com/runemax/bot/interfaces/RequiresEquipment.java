package com.runemax.bot.interfaces;

import net.runelite.api.EquipmentInventorySlot;

import java.util.HashMap;

public interface RequiresEquipment {
    HashMap<EquipmentInventorySlot, Integer> getEquipmentRequired();
}
