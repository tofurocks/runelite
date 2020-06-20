package com.runemax.bot.api.tabs.magic;

import com.runemax.bot.api.wrappers.Interactable;
import com.runemax.bot.api.varps.Varps;

public class Magic {
    public static final int SPELLBOOK_MODERN = 0;
    public static final int SPELLBOOK_ANCIENT = 1;
    public static final int SPELLBOOK_LUNAR = 2;
    public static final int SPELLBOOK_ARCEUUS = 3;

    public static boolean isAutoCasting() {
        return Varps.get(108) != 0;
    }

    public static boolean isSpellSelected(Spell spell){
        return spell.getWidget().getBorderType() == 2;
    }

    public static boolean cast(Spell spell, Interactable target){
        if(!isSpellSelected(spell)){
            cast(spell);
            return false;
        }

        target.interactSpellOn();
        return true;
    }

    public static void cast(Spell spell){
        spell.getWidget().interact(0);
    }

    public static boolean isAutocasting(){
        return Varps.get(108) != 0;
    }
}
