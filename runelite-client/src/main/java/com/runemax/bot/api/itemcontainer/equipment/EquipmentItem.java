package com.runemax.bot.api.itemcontainer.equipment;

import com.runemax.bot.api.exception.RickkPointerException;
import com.runemax.bot.api.itemcontainer.ContainerItem;
import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.MenuOpcode;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.WidgetInfo;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EquipmentItem extends ContainerItem {
    @Getter
    @Nullable
    private EquipmentInventorySlot slot;

    @Getter
    @Nullable
    private WidgetInfo widgetInfo;

    public EquipmentItem(Item rl, int index) {
        super(rl, index);

        if (index == -1) {
            slot = null;
            return;
        }

        for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
            if (value.getSlotIdx() == index) {
                slot = value;
                break;
            }
        }

        if(slot == null) {
            throw new IllegalArgumentException("no slot for index:" + index);
        }

        for (KitType kitType : KitType.values()) {
            if (kitType.getIndex() == slot.getSlotIdx()) {
                widgetInfo = kitType.getWidgetInfo();
                break;
            }
        }

        if(widgetInfo == null) {
            throw new IllegalArgumentException("no widgetInfo for index:" + index);
        }

    }

    public void unequip() {
        interact(0);
    }

    @Override
    public int getMenuIdentifier(Integer actionIndex) {
        return actionIndex + 1;
    }

    @Override
    public int getMenuType(int actionIndex) {
        return actionIndex > 4 ? MenuOpcode.CC_OP_LOW_PRIORITY.getId() : MenuOpcode.CC_OP.getId();
    }

    @Override
    public int getMenuParam0() {
        return -1;
    }

    @Override
    public int getMenuParam1() {
        EquipmentInventorySlot slot = this.slot;
        if (slot == null) throw new RickkPointerException();
        return widgetInfo.getPackedId();
    }

    @Deprecated
    @Override
    public String[] getActions() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void interact(Predicate<String> actionPredicate) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean hasAction(String actionContainsIgnoreCase) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void interact(String actionContains) {
        throw new UnsupportedOperationException();
    }
}
