package com.runemax.bot.api.wrappers;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.interact.Interact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public interface Interactable extends Actionable{
    Logger log = LoggerFactory.getLogger(Interactable.class);

    int getMenuIdentifier(Integer actionIndex);

    int getMenuType(int actionIndex);

    int getMenuParam0();

    int getMenuParam1();

    default int getMenuTypeForUseItemOn() {
        throw new UnsupportedOperationException();
    }

    default int getMenuTypeForCastSpellOn() {
        throw new UnsupportedOperationException();
    }

    default String getMenuTarget() {
        return "";
    }

    default String getMenuOption() {
        return "";
    }

    default void interact(int menuType, Integer actionIndex) {
        Interact.interact(getMenuTarget(), getMenuOption(), getMenuIdentifier(actionIndex), menuType, getMenuParam0(), getMenuParam1());
    }

    default void interact(int actionIndex) {
        interact(getMenuType(actionIndex), actionIndex);
    }

    default void interact(Predicate<String> actionPredicate) {
        String[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            if (action != null && action.length() != 0 && actionPredicate.test(action)) {
                interact(i);
                return;
            }
        }

        throw new BotException("no action matched");
    }

    default void interact(String actionContainsIgnoreCase) {
        interact(a -> a.toLowerCase().contains(actionContainsIgnoreCase.toLowerCase()));
    }

    default void interactItemOn(){
        interact(getMenuTypeForUseItemOn(), null);
    }

    default void interactSpellOn(){
        interact(getMenuTypeForCastSpellOn(), null);
    }
}
