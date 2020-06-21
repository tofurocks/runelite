package com.runemax.bot.api.itemcontainer.bank;

import java.util.Objects;
import java.util.function.Predicate;
import net.runelite.api.Item;
import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.itemcontainer.ContainerItem;

public class BankInvItem extends ContainerItem {
    public BankInvItem(Item delegate, int index) {
        super(delegate, index);
    }

    @Override
    public int getMenuIdentifier(Integer actionIndex) {
        return Objects.requireNonNull(actionIndex);
    }

    @Override
    public int getMenuType(int actionIndex) {
        return MenuOpcode.CC_OP.getId();
    }

    @Override
    public int getMenuParam0() {
        return getIndex();
    }

    @Override
    public int getMenuParam1() {
        return WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId();
    }

    @Override
    @Deprecated
    public String[] getActions() {
         throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void interact(Predicate<String> actionPredicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void interact(String actionContains) {
        throw new UnsupportedOperationException();
    }
}
