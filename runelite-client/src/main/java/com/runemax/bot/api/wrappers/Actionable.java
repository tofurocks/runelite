package com.runemax.bot.api.wrappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Actionable {
    String[] getActions();

    default List<String> getTrimmedActions() {
        String[] actions = getActions();

        if (actions == null) return Collections.emptyList();

        return Arrays.stream(actions).filter(a -> a != null && !a.isEmpty()).collect(Collectors.toList());
    }

    default boolean hasAction(Predicate<String> actionPredicate) {
        return getTrimmedActions().stream().anyMatch(actionPredicate);
    }

    default boolean hasAction(String actionContainsIgnoreCase) {
        return hasAction(a -> a.toLowerCase().contains(actionContainsIgnoreCase.toLowerCase()));
    }
}
