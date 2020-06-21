package com.runemax.bot.api.itemcontainer.bank;

import com.runemax.bot.api.itemcontainer.ContainerItem;
import com.runemax.bot.api.widget.Dialog;
import net.runelite.api.Item;
import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.WidgetInfo;

import java.util.Objects;
import java.util.function.Predicate;

public class BankItem extends ContainerItem {
    public BankItem(Item item, int index) {
        super(item, index);
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
        return WidgetInfo.BANK_ITEM_CONTAINER.getId();
    }

    @Override
    public String[] getActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void interact(Predicate<String> actionPredicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void interact(String actionContains) {
        throw new UnsupportedOperationException();
    }

    private enum WithdrawOption {
        ONE(1),
        FIVE(3),
        TEN(4),
        DEFAULT_QUANTITY(5),
        X(6),
        ALL(7),
        ALL_BUT_1(8);

        private final int menuIndex;

        WithdrawOption(int menuIndex) {
            this.menuIndex = menuIndex;
        }

        public int getMenuIndex() {
            return menuIndex;
        }
    }

    public void withdraw(int quantity){
        WithdrawOption withdrawOption;
        if (quantity < 1) {
            throw new IllegalArgumentException("tryna withdraw quantity < 1");
        } else if (quantity == 1) {
            withdrawOption = WithdrawOption.ONE;
        } else if (quantity == 5) {
            withdrawOption = WithdrawOption.FIVE;
        } else if (quantity == 10) {
            withdrawOption = WithdrawOption.TEN;
        } else if (quantity == Bank.getPresetQuantity()) {
            withdrawOption = WithdrawOption.DEFAULT_QUANTITY;
        } else if (quantity >= getQuantity()) {
            withdrawOption = WithdrawOption.ALL;
        }else if(quantity == getQuantity() - 1){
            withdrawOption = WithdrawOption.ALL_BUT_1;
        } else {
            withdrawOption = WithdrawOption.X;
        }

        interact(withdrawOption.getMenuIndex());

        if (withdrawOption == WithdrawOption.X) {
            Dialog.enterAmount(quantity);
        }
    }

    public void withdraw(){
        withdraw(1);
    }

    public void withdrawAll(){
        withdraw(Integer.MAX_VALUE);
    }
}
