package com.runemax.bot.api.itemcontainer.inventory;

import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.itemcontainer.ContainerItem;

public class InventoryItem extends ContainerItem {
    public InventoryItem(net.runelite.api.Item delegate, int index) {
        super(delegate, index);
    }

    public boolean isNoted(){
        return getNote() == 799;
    }


    @Override
    public int getMenuIdentifier( Integer actionIndex) {
        return getId();
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (actionIndex) {
            case 0:
                if (getActions()[0].equals("Use")) {
                    return MenuOpcode.ITEM_USE.getId();
                }
                return MenuOpcode.ITEM_FIRST_OPTION.getId();
            case 1:
                return MenuOpcode.ITEM_SECOND_OPTION.getId();
            case 2:
                return MenuOpcode.ITEM_THIRD_OPTION.getId();
            case 3:
                return MenuOpcode.ITEM_FOURTH_OPTION.getId();
            case 4:
                return MenuOpcode.ITEM_FIFTH_OPTION.getId();
            default:
                throw new IllegalArgumentException("no MenuOpcode(aka opcode) for inventory item action index " + actionIndex);
        }
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId();
    }

    @Override
    public int getMenuTypeForCastSpellOn() {
        return MenuOpcode.ITEM_USE_ON_WIDGET.getId();
    }//32 rl named wrong i think

    @Override
    public int getMenuParam0() {
        return getIndex();
    }

    @Override
    public int getMenuParam1() {
        return WidgetInfo.INVENTORY.getId();
    }

    @Override
    public String[] getActions() {
        String[] actions = getInventoryActions();
        if (actions[0] == null) {
            actions[0] = "Use";
        }
        return actions;
    }
}
