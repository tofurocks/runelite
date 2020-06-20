package com.runemax.bot.scripts.generic;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.Task;

public class ToggleRun extends Task {
    int threshold = 100;

    @Override
    public boolean activate() {
        if(Bank.isOpen()){
            return false;
        }
        return !Movement.isRunEnabled() && Movement.getRunEnergy() >= threshold;
    }

    @Override
    public int execute() {
        Movement.toggleRun();
        threshold = Rand.nextInt(1, 60);
        return 0;
    }
}
