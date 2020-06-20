package com.runemax.bot.api.itemcontainer;

import com.runemax.bot.api.commons.By;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.OnGameThread;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public class ItemContainer<I extends ContainerItem> {
    private final InventoryID inventoryID;
    private final BiFunction<Item, Integer, I> mapper;

    public ItemContainer(InventoryID inventoryID, BiFunction<Item, Integer, I> mapper) {
        this.inventoryID = inventoryID;
        this.mapper = mapper;
    }

    @Nonnull
    public List<I> all(Predicate<? super I> predicate) {
        return OnGameThread.invokeAndWait(() -> {
            List<I> out = new ArrayList<>();
            net.runelite.api.ItemContainer itemContainer = Client.getInstance().getItemContainer(inventoryID);
            if (itemContainer == null) {
                return out;
            }

            Item[] items = itemContainer.getItems();
            for (int i = 0; i < items.length; i++) {
                Item item = items[i];

                if (item == null || item.getId() == -1) {
                    continue;
                }

                I tableItem = mapper.apply(item, i);
                if (tableItem.getName().equals("null") || !predicate.test(tableItem)) {
                    continue;
                }

                out.add(tableItem);
            }

            return out;
        });
    }

    @Nonnull
    public List<I> all() {
        return all(o -> true);
    }

    @Nonnull
    protected Stream<I> stream() {
        return all().stream();
    }

    @Nonnull
    public I best(Predicate<? super I> predicate, Comparator<? super I> comparator) {
        return stream().filter(predicate).max(comparator).orElse(mapper.apply(null, -1));
    }

    @Nonnull
    public I first(Predicate<? super I> predicate) {
        return stream().filter(predicate).findFirst().orElse(mapper.apply(null, -1));
    }

    @Nonnull
    public I first(int... ids) {
        return first(By.id(ids));
    }

    @Nonnull
    public I first(String... names) {
        return first(By.name(names));
    }

    @Nonnull
    public I random(Predicate<? super I> predicate) {
        return best(predicate, (o1, o2) -> Rand.nextInt());
    }

    @Nonnull
    public I random(int... ids) {
        return random(By.id(ids));
    }

    @Nonnull
    public I random(String... name) {
        return random(By.name(name));
    }

    @Nonnull
    public I atIndex(int index) {
        return stream().filter(i -> i.getIndex() == index).findAny().orElse(mapper.apply(null, -1));
    }

    public int count(Predicate<? super I> filter) {
        return stream().filter(filter).mapToInt(ContainerItem::getQuantity).sum();
    }

    public int count(int... ids) {
        return count(By.id(ids));
    }

    public int count(String... names) {
        return count(By.name(names));
    }

    public int distinctCount(Predicate<? super I> predicate) {
        return all(predicate).size();
    }

    public int distinctCount() {
        return distinctCount(x -> true);
    }

    public int distinctCount(int... ids) {
        return distinctCount(By.id(ids));
    }

    public int distinctCount(String... names) {
        return distinctCount(By.name(names));
    }

    public boolean isEmpty() {
        return distinctCount() == 0;
    }

    public boolean containsAll(int...ids){
        HashSet<Integer> idSet = new HashSet<>();
        for (int id : ids) {
            idSet.add(id);
        }

        return containsAll(idSet);
    }
    public boolean containsAll(Collection<Integer> ids){
        for (I i : all(i -> ids.contains(i.getId()))) {
            ids.remove(i.getId());
        }

        return ids.isEmpty();
    }
}
