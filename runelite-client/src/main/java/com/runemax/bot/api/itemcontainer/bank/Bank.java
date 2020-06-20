package com.runemax.bot.api.itemcontainer.bank;

import com.runemax.bot.api.commons.By;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.itemcontainer.ItemContainer;
import com.runemax.bot.api.varps.Varps;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class Bank {
    private enum DepositOption {
        ONE(2),
        FIVE(4),
        TEN(5),
        DEFAULT_QUANTITY(6),
        X(7),
        ALL(8);

        private final int menuIndex;

        DepositOption(int menuIndex) {
            this.menuIndex = menuIndex;
        }

        public int getMenuIndex() {
            return menuIndex;
        }
    }

    private static final ItemContainer<BankItem> BANK_ITEM_CONTAINER = new ItemContainer<BankItem>(InventoryID.BANK, BankItem::new) {
        @Nonnull
        @Override
        public List<BankItem> all() {
            List<BankItem> superAll = super.all();
            superAll.removeIf(bankItem -> bankItem.getQuantity() == 0);
            Collections.reverse(superAll);
            return superAll;
        }
    };

    private static final ItemContainer<BankInvItem> BANK_INV_ITEM_CONTAINER = new ItemContainer<>(InventoryID.INVENTORY, BankInvItem::new);

    public static boolean isOpen() {
        Widget widget = Widgets.get(WidgetInfo.BANK_ITEM_CONTAINER);
        return widget.isPresent() && !widget.isHidden();
    }

    public static void close() {
        Movement.walk(Players.getLocal());//shit kept breaking so fuck it
    }

    public static void openBooth() {
        TileObjects.closest(o -> o.getName().toLowerCase().contains("bank booth") || o.hasAction("bank")).interact(a -> a.equalsIgnoreCase("bank") || a.equalsIgnoreCase("use"));
    }

    public static void openChest() {
        TileObjects.closest("Bank chest").interact("Use");
    }

    public static void open() {
        TileObjects.closest(o -> o.hasAction("Bank") || (o.hasAction("Use") && o.getName().toLowerCase().contains("bank"))).interact(a -> a.equals("Use") || a.equals("Bank"));
    }

    public static int getPresetQuantity() {
        return Varps.getBit(3960);
    }

    private static final WidgetQuery depositInv = new WidgetQuery(WidgetID.BANK_GROUP_ID, w->w.hasAction("Deposit inventory"));
    public static void depositInventory() {
        depositInv.get().interact("Deposit inventory");
    }

    private static final WidgetQuery depositEquip = new WidgetQuery(WidgetID.BANK_GROUP_ID, w->w.hasAction("Deposit worn items"));
    public static void depositEquipment() {
        depositEquip.get().interact("Deposit worn items");
    }

    public static boolean isNotedWithdrawMode() {
        return Varps.getBit(3958) == 1;
    }

    public static final WidgetQuery note = new WidgetQuery(WidgetID.BANK_GROUP_ID, w-> w.hasAction("Note"));
    public static final WidgetQuery item = new WidgetQuery(WidgetID.BANK_GROUP_ID, w->w.hasAction("Item"));
    public static void setWithdrawMode(boolean noted) {
        if(noted){
            note.get().interact("Note");
        }else {
            item.get().interact("Item");
        }
    }

    public static boolean isAlwaysSetPlaceholders() {
        return Varps.getBit(3755) == 1;
    }

    public static void toggleAlwaysSetPlaceholders(boolean on) {
        Widgets.get(WidgetID.BANK_GROUP_ID, 37).interact(on ? "Enable" : "Disable");
    }

    public static int getWithdrawXQuantity() {
        return Varps.getBit(3960);
    }

    public static boolean isQuantityModeOne() {
        return Varps.getBit(6590) == 0;
    }

    public static final WidgetQuery setQuantityModeOne = new WidgetQuery(12, w->w.hasAction("Default quantity: 1"));
    public static void setQuantityModeOne() {
        setQuantityModeOne.get().interact("Default quantity: 1");
    }

    public static void deposit(Predicate<? super BankInvItem> predicate, int quantity) {
        if (!isQuantityModeOne()) {
            setQuantityModeOne();
            throw new BotException("setting quantity mode to one");
        }

        DepositOption depositOption;
        if (quantity < 1) {
            throw new IllegalArgumentException("tryna deposit quantity < 1");
        } else if (quantity == 1) {
            depositOption = DepositOption.ONE;
        } else if (quantity == 5) {
            depositOption = DepositOption.FIVE;
        } else if (quantity == 10) {
            depositOption = DepositOption.TEN;
        } else if (quantity == getWithdrawXQuantity()) {
            depositOption = DepositOption.DEFAULT_QUANTITY;
        } else if (quantity >= BANK_INV_ITEM_CONTAINER.count(predicate)) {
            depositOption = DepositOption.ALL;
        } else {
            depositOption = DepositOption.X;
        }

        BANK_INV_ITEM_CONTAINER.random(predicate).interact(depositOption.getMenuIndex());

        if (depositOption != DepositOption.X) {
            return;
        }

        Dialog.enterAmount(quantity);
    }

    public static void deposit(int id, int quantity) {
        deposit(item -> item.getId() == id, quantity);
    }

    public static void deposit(String name, int quantity) {
        deposit(item -> item.getName().equalsIgnoreCase(name), quantity);
    }

    public static void depositAll(Predicate<? super BankInvItem> predicate) {
        Set<Integer> seenIds = new HashSet<>();
        for (BankInvItem item : BANK_INV_ITEM_CONTAINER.all(predicate)) {
            if (!seenIds.add(item.getId())) continue;

            item.interact(DepositOption.ALL.getMenuIndex());

            Sleep.sleep(200);
            Sleep.until(Interact::isReady, 200);
        }
    }

    public static void depositAll(int id) {
        BANK_INV_ITEM_CONTAINER.first(id).interact(DepositOption.ALL.getMenuIndex());
    }

    public static void depositAll(String name) {
        BANK_INV_ITEM_CONTAINER.first(name).interact(DepositOption.ALL.getMenuIndex());
    }

    public static void depositAllExcept(Predicate<? super BankInvItem> predicate) {
        depositAll(predicate.negate());
    }

    public static void depositAllExcept(int... ids) {
        depositAllExcept(By.id(ids));
    }

    public static void depositAllExcept(String... names) {
        depositAllExcept(By.name(names));
    }

    public static class NotInBankException extends BotException {
    }

    public static void withdraw(Predicate<? super BankItem> predicate, int quantity, boolean noted) {
        BankItem first = Bank.first(predicate);
        if (first.isEmpty()) {
            throw new NotInBankException();
        }

        if (isNotedWithdrawMode() != noted) {
            setWithdrawMode(noted);
            Sleep.until(() -> isNotedWithdrawMode() == noted, 5000);
        }

        if (!Bank.isQuantityModeOne()) {
            Bank.setQuantityModeOne();
            Sleep.until(Bank::isQuantityModeOne, 5000);
        }

        first.withdraw(quantity);
    }

    public static void withdraw(int id, int quantity, boolean noted) {
        withdraw(By.id(id), quantity, noted);
    }

    public static void withdraw(String name, int quantity, boolean noted) {
        withdraw(By.name(name), quantity, noted);
    }

    public static void withdraw(Predicate<? super BankItem> predicate, boolean noted) {
        withdraw(predicate, 1, noted);
    }

    public static void withdraw(Predicate<? super BankItem> predicate, int quantity) {
        withdraw(predicate, quantity, false);
    }

    public static void withdraw(int id, int quantity) {
        withdraw(id, quantity, false);
    }

    public static void withdraw(String name, int quantity) {
        withdraw(name, quantity, false);
    }

    public static void withdraw(Predicate<? super BankItem> predicate) {
        withdraw(predicate, 1);
    }

    public static void withdraw(int id) {
        withdraw(By.id(id));
    }

    public static void withdraw(String name) {
        withdraw(By.name(name));
    }

    public static void withdrawAll(Predicate<? super BankItem> predicate, boolean noted) {
        if (isNotedWithdrawMode() != noted) {
            setWithdrawMode(noted);
            Sleep.until(() -> Bank.isNotedWithdrawMode() == noted, 2000);
        }

        boolean withdrew = false;
        for (BankItem item : all(predicate)) {
            if (Inventory.isFull()) return;

            item.withdrawAll();
            withdrew = true;

            Sleep.sleep(200);
            Sleep.until(Interact::isReady, 200);
        }

        if (!withdrew) throw new NotInBankException();
    }

    public static void withdrawAll(int id, boolean noted) {
        withdraw(id, Integer.MAX_VALUE, noted);
    }

    public static void withdrawAll(String name, boolean noted) {
        withdraw(name, Integer.MAX_VALUE, noted);
    }

    public static void withdrawAll(Predicate<? super BankItem> predicate) {
        withdrawAll(predicate, false);
    }

    public static void withdrawAll(int id) {
        withdrawAll(id, false);
    }

    public static void withdrawAll(String name) {
        withdrawAll(name, false);
    }

    public static List<BankItem> all(Predicate<? super BankItem> predicate) {
        List<BankItem> all = BANK_ITEM_CONTAINER.all(predicate);
        Collections.reverse(all);
        return all;
    }

    //generated delegates, move up if modified
    @Nonnull
    public static List<BankItem> all() {
        return BANK_ITEM_CONTAINER.all();
    }

    @Nonnull
    public static BankItem best(Predicate<? super BankItem> predicate, Comparator<? super BankItem> comparator) {
        return BANK_ITEM_CONTAINER.best(predicate, comparator);
    }

    @Nonnull
    public static BankItem first(Predicate<? super BankItem> predicate) {
        return BANK_ITEM_CONTAINER.first(predicate);
    }

    @Nonnull
    public static BankItem first(int... ids) {
        return BANK_ITEM_CONTAINER.first(ids);
    }

    @Nonnull
    public static BankItem first(String... names) {
        return BANK_ITEM_CONTAINER.first(names);
    }

    @Nonnull
    public static BankItem random(Predicate<? super BankItem> predicate) {
        return BANK_ITEM_CONTAINER.random(predicate);
    }

    @Nonnull
    public static BankItem random(int... ids) {
        return BANK_ITEM_CONTAINER.random(ids);
    }

    @Nonnull
    public static BankItem random(String... name) {
        return BANK_ITEM_CONTAINER.random(name);
    }

    @Nonnull
    public static BankItem atIndex(int index) {
        return BANK_ITEM_CONTAINER.atIndex(index);
    }

    public static int count(Predicate<? super BankItem> filter) {
        return BANK_ITEM_CONTAINER.count(filter);
    }

    public static int count(int... ids) {
        return BANK_ITEM_CONTAINER.count(ids);
    }

    public static int count(String... names) {
        return BANK_ITEM_CONTAINER.count(names);
    }

    public static int distinctCount(Predicate<? super BankItem> predicate) {
        return BANK_ITEM_CONTAINER.distinctCount(predicate);
    }

    public static int distinctCount() {
        return BANK_ITEM_CONTAINER.distinctCount();
    }

    public static int distinctCount(int... ids) {
        return BANK_ITEM_CONTAINER.distinctCount(ids);
    }

    public static int distinctCount(String... names) {
        return BANK_ITEM_CONTAINER.distinctCount(names);
    }

    public static boolean isEmpty() {
        return BANK_ITEM_CONTAINER.isEmpty();
    }

    public static boolean containsAll(int... ids) {
        return BANK_ITEM_CONTAINER.containsAll(ids);
    }

    public static boolean containsAll(Collection<Integer> ids) {
        return BANK_ITEM_CONTAINER.containsAll(ids);
    }
}
