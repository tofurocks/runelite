package com.runemax.bot.scripts.generic;

import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.Dialog;

import static com.runemax.bot.api.commons.Rand.gaussian;

public class  ContinueDialog extends Task {
    @Override
    public boolean activate() {
        return Dialog.canContinue();
    }

    @Override
    public int execute() {
        Dialog.continueSpace();
        return gaussian(25, 15000, 100, 50);
    }
}
