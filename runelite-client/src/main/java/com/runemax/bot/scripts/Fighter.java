package com.runemax.bot.scripts;


import com.runemax.bot.api.data.VarpData;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.scripts.combat.AutoFight;
import com.runemax.bot.scripts.combat.cows.EquipMagicGear;
import com.runemax.bot.scripts.combat.cows.WalkToCows;
import com.runemax.bot.scripts.combat.muling.GetMagicSupplies;
import com.runemax.bot.scripts.combat.seagulls.WalkToSeagulls;
import com.runemax.bot.scripts.generic.ContinueDialog;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.runemax.bot.api.commons.Sleep.sleep;

@ScriptMeta("Fighter")
@Slf4j
public class Fighter extends BotScript {
    final List<Task> tasks = new ArrayList<>();
    boolean doneStartup = false;
    Timer timer;

    @Override
    protected void loop() {
        if (Client.getGameState() != GameState.LOGGED_IN) {
            log.info("We are not logged in");
            sleep(5000);
            return;
        }
        if (!doneStartup) {
            doStartup();
            log.info("Setting done startup to true");
            doneStartup = true;
        }
        for (Task task : tasks) {
            if (task.activate()) {
                log.info("Executing " + task.getClass().getSimpleName());
                int wait = task.execute();
                if (wait < 0) {
                    log.info("Stopping script");
                    stopLooping();
                    return;
                }
                sleep(wait);
                return;
            }
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        timer.cancel();
        timer.purge();
    }

    void doStartup() {
        log.info("Doing startup");
        if (timer != null) {
            log.info("Timer already exists"); //TODO: not sure if this is reachable
            timer.cancel();
            timer.purge();
        }
        timer = new Timer("Timer");

        sleep(5000);
        if (Varps.get(VarpData.TUT_PROGRESS) != 1000) {
            log.info("We are not done the tutorial yet, stopping");
            this.stopLooping();
            return;
        }
        int magic = Skills.getLevel(Skill.MAGIC);
        if(magic < 45){
            log.info("Adding fighting tasks");
            if (!Equipment.isOccupied(EquipmentInventorySlot.WEAPON) && Inventory.first("Bronze sword").isPresent()) {
                log.info("Equipping bronze sword");
                Inventory.first("Bronze sword").interact("Wield");
            }
            /** Seagull and generic tasks */
            tasks.add(new WalkToSeagulls());
            tasks.add(new ContinueDialog());
            /** Cow training tasks */
            tasks.add(new GetMagicSupplies());
            tasks.add(new EquipMagicGear());
            tasks.add(new WalkToCows());
            tasks.add(new AutoFight("Seagull"));
        }

    }
}
