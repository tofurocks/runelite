package com.runemax.bot.api.commons;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.runemax.bot.api.wrappers.Interactable;
import com.runemax.bot.api.wrappers.item.Item;
import com.runemax.bot.api.wrappers.Identifiable;
import com.runemax.bot.api.wrappers.Nameable;

public class By {
    public static Predicate<Nameable> name(String...names){
        Set<String> nameSet = Arrays.stream(names).map(String::toLowerCase).collect(Collectors.toSet());
        return nameable -> {
            String name = nameable.getName();
            return name != null && nameSet.contains(name.toLowerCase());
        };
    }

    public static Predicate<Identifiable> id(int... ids){
        return identifiable -> Arrays.stream(ids).anyMatch(id -> id == identifiable.getId());
    }

    public static Predicate<Identifiable> idBox(Integer... ids){//nigga
        return identifiable -> Arrays.stream(ids).anyMatch(id -> id == identifiable.getId());
    }

    public static Predicate<Item> idIgnoreNote(int... ids){
        return item -> Arrays.stream(ids).anyMatch(id -> item.getId() == id || item.getLinkedNoteId() == id);
    }

    public static Predicate<Nameable> prefix(String... prefixes){
        return identifiable -> {
            String name = identifiable.getName();
            if(name == null) return false;

            final String lowerCaseName = name.toLowerCase();
            return Arrays.stream(prefixes).anyMatch(prefix -> lowerCaseName.startsWith(prefix.toLowerCase()));
        };
    }

    public static Predicate<Interactable> action(String actions){
        return interactable-> interactable.hasAction(actions);
    }
}
