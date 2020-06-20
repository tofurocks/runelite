package com.runemax.bot.api.itemcontainer.equipment;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import com.runemax.bot.api.itemcontainer.ItemContainer;

import javax.annotation.Nonnull;

public class Equipment {


    private static final ItemContainer<EquipmentItem> EQUIPMENT_ITEM_CONTAINER = new ItemContainer<>(InventoryID.EQUIPMENT, EquipmentItem::new);

    public static EquipmentItem inSlot(EquipmentInventorySlot slot) {
        return atIndex(slot.getSlotIdx());
    }

    public static boolean isOccupied(EquipmentInventorySlot slot) {
        return !inSlot(slot).isEmpty();
    }

    //generated delegates, move up if modified
    @Nonnull
    public static List<EquipmentItem> all(Predicate<? super EquipmentItem> predicate) {
        return EQUIPMENT_ITEM_CONTAINER.all(predicate);
    }

    @Nonnull
    public static List<EquipmentItem> all() {
        return EQUIPMENT_ITEM_CONTAINER.all();
    }

    @Nonnull
    public static EquipmentItem best(Predicate<? super EquipmentItem> predicate, Comparator<? super EquipmentItem> comparator) {
        return EQUIPMENT_ITEM_CONTAINER.best(predicate, comparator);
    }

    @Nonnull
    public static EquipmentItem first(Predicate<? super EquipmentItem> predicate) {
        return EQUIPMENT_ITEM_CONTAINER.first(predicate);
    }

    @Nonnull
    public static EquipmentItem first(int... ids) {
        return EQUIPMENT_ITEM_CONTAINER.first(ids);
    }

    @Nonnull
    public static EquipmentItem first(String... names) {
        return EQUIPMENT_ITEM_CONTAINER.first(names);
    }

    @Nonnull
    public static EquipmentItem random(Predicate<? super EquipmentItem> predicate) {
        return EQUIPMENT_ITEM_CONTAINER.random(predicate);
    }

    @Nonnull
    public static EquipmentItem random(int... ids) {
        return EQUIPMENT_ITEM_CONTAINER.random(ids);
    }

    @Nonnull
    public static EquipmentItem random(String... name) {
        return EQUIPMENT_ITEM_CONTAINER.random(name);
    }

    @Nonnull
    public static EquipmentItem atIndex(int index) {
        return EQUIPMENT_ITEM_CONTAINER.atIndex(index);
    }

    public static int count(Predicate<? super EquipmentItem> filter) {
        return EQUIPMENT_ITEM_CONTAINER.count(filter);
    }

    public static int count(int... ids) {
        return EQUIPMENT_ITEM_CONTAINER.count(ids);
    }

    public static int count(String... names) {
        return EQUIPMENT_ITEM_CONTAINER.count(names);
    }

    public static int distinctCount(Predicate<? super EquipmentItem> predicate) {
        return EQUIPMENT_ITEM_CONTAINER.distinctCount(predicate);
    }

    public static int distinctCount() {
        return EQUIPMENT_ITEM_CONTAINER.distinctCount();
    }

    public static int distinctCount(int... ids) {
        return EQUIPMENT_ITEM_CONTAINER.distinctCount(ids);
    }

    public static int distinctCount(String... names) {
        return EQUIPMENT_ITEM_CONTAINER.distinctCount(names);
    }

    public static boolean isEmpty() {
        return EQUIPMENT_ITEM_CONTAINER.isEmpty();
    }

    public static boolean containsAll(int... ids) {
        return EQUIPMENT_ITEM_CONTAINER.containsAll(ids);
    }

    public static boolean containsAll(Collection<Integer> ids) {
        return EQUIPMENT_ITEM_CONTAINER.containsAll(ids);
    }
}
