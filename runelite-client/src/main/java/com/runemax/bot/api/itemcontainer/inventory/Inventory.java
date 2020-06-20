package com.runemax.bot.api.itemcontainer.inventory;

import com.runemax.bot.api.commons.By;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.ItemContainer;
import com.runemax.bot.api.wrappers.Interactable;
import net.runelite.api.InventoryID;
import net.runelite.api.MenuAction;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public class Inventory {
    private static final ItemContainer<InventoryItem> INVENTORY_ITEM_CONTAINER = new ItemContainer<>(InventoryID.INVENTORY, InventoryItem::new);

    public static boolean isItemSelected() {
        return Client.getIsItemSelected() == 1;
    }

    public static void useOn(Predicate<? super InventoryItem> predicate, Interactable target) {
        Inventory.first(predicate).interact(MenuAction.ITEM_USE.getId(), null);

        if(!Sleep.untilWithConfirm(()-> Interact.isReady() && Inventory.isItemSelected(), 1000)) {
            throw new BotException("item never selected");
        }

        target.interact(target.getMenuTypeForUseItemOn(), null);
    }

    public static boolean isFull() {
        return distinctCount() == 28;
    }

    public static void dropOne(Predicate<? super InventoryItem> inventoryItemPredicate) {
        random(inventoryItemPredicate).interact("Drop");
    }

    public static void dropOne(int... id) {
         dropOne(By.id(id));
    }

    public static void dropOne(String name) {
         dropOne(By.name(name));
    }

    public static void dropAll(Predicate<? super InventoryItem> inventoryItemPredicate) {
        List<InventoryItem> items = all(inventoryItemPredicate);
        Collections.shuffle(items);

        for (InventoryItem inventoryItem : items) {
            inventoryItem.interact("Drop");
            Sleep.sleep(100, 200);
            Sleep.until(Interact::isReady, 20, 200);
        }
    }

    public static void dropAll(int... ids) {
         dropAll(By.id(ids));
    }

    public static void dropAll(String... names) {
         dropAll(By.name(names));
    }

    public static void dropAllExcept(Predicate<? super InventoryItem> inventoryItemPredicate) {
         dropAll(inventoryItemPredicate.negate());
    }

    public static void dropAllExcept(int... ids) {
         dropAllExcept(By.id(ids));
    }

    public static void dropAllExcept(String... names) {
         dropAllExcept(By.name(names));
    }



    //generated delegates, move up if modified
    public static boolean containsAll(Collection<Integer> ids) {
        return INVENTORY_ITEM_CONTAINER.containsAll(ids);
    }

    @Nonnull
    public static List<InventoryItem> all(Predicate<? super InventoryItem> predicate) {
        return INVENTORY_ITEM_CONTAINER.all(predicate);
    }

    @Nonnull
    public static List<InventoryItem> all() {
        return INVENTORY_ITEM_CONTAINER.all();
    }

    @Nonnull
    public static InventoryItem best(Predicate<? super InventoryItem> predicate, Comparator<? super InventoryItem> comparator) {
        return INVENTORY_ITEM_CONTAINER.best(predicate, comparator);
    }

    @Nonnull
    public static InventoryItem first(Predicate<? super InventoryItem> predicate) {
        return INVENTORY_ITEM_CONTAINER.first(predicate);
    }

    @Nonnull
    public static InventoryItem first(int... ids) {
        return INVENTORY_ITEM_CONTAINER.first(ids);
    }

    @Nonnull
    public static InventoryItem first(String... names) {
        return INVENTORY_ITEM_CONTAINER.first(names);
    }

    @Nonnull
    public static InventoryItem random(Predicate<? super InventoryItem> predicate) {
        return INVENTORY_ITEM_CONTAINER.random(predicate);
    }

    @Nonnull
    public static InventoryItem random(int... ids) {
        return INVENTORY_ITEM_CONTAINER.random(ids);
    }

    @Nonnull
    public static InventoryItem random(String... name) {
        return INVENTORY_ITEM_CONTAINER.random(name);
    }

    public static int count(Predicate<? super InventoryItem> filter) {
        return INVENTORY_ITEM_CONTAINER.count(filter);
    }

    public static int count(int... ids) {
        return INVENTORY_ITEM_CONTAINER.count(ids);
    }

    public static int count(String... names) {
        return INVENTORY_ITEM_CONTAINER.count(names);
    }

    public static int distinctCount(Predicate<? super InventoryItem> predicate) {
        return INVENTORY_ITEM_CONTAINER.distinctCount(predicate);
    }

    public static int distinctCount() {
        return INVENTORY_ITEM_CONTAINER.distinctCount();
    }

    public static int distinctCount(int... ids) {
        return INVENTORY_ITEM_CONTAINER.distinctCount(ids);
    }

    public static int distinctCount(String... names) {
        return INVENTORY_ITEM_CONTAINER.distinctCount(names);
    }

    @Nonnull
    public static InventoryItem atIndex(int slot) {
        return INVENTORY_ITEM_CONTAINER.atIndex(slot);
    }

    public static boolean isEmpty() {
        return INVENTORY_ITEM_CONTAINER.isEmpty();
    }

    public static boolean containsAll(int... ids) {
        return INVENTORY_ITEM_CONTAINER.containsAll(ids);
    }

    public static boolean containsAll(Set<Integer> idSet){
        return INVENTORY_ITEM_CONTAINER.containsAll(idSet);
    }
}
