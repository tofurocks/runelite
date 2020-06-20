package com.runemax.bot.scripts.combat;


import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.input.Keyboard;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.worlds.Worlds;
import com.runemax.bot.api.wrappers.entity.actor.Actor;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.widgets.WidgetID;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.runemax.bot.Constants.LUM_COWS;
import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class AutoFight extends Task {
    Predicate<NPC> filter;
    int runThreshold = Rand.nextInt(1, 101);
    final WidgetQuery OPEN_COMBAT_TAB = new WidgetQuery(548, widget -> widget.hasAction("combat"));
    final WidgetQuery SELECT_STAB_STYLE = new WidgetQuery(593, widget -> widget.hasAction("stab"));
    final WidgetQuery SELECT_CHOP_STYLE = new WidgetQuery(593, widget -> widget.hasAction("chop"));
    final WidgetQuery SELECT_BLOCK_STYLE = new WidgetQuery(593, widget -> widget.hasAction("block"));
    final WidgetQuery SELECT_LUNGE_STYLE = new WidgetQuery(593, widget -> widget.hasAction("lunge"));

    private final WidgetQuery HOME_TELEPORT = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Lumbridge Home Teleport"));

    public AutoFight(String... targetNames) {
        this.filter = npc -> Arrays.asList(targetNames).contains(npc.getName());
    }

    public AutoFight(Predicate<NPC> filter) {
        this.filter = filter;
    }

    @Override
    public boolean activate() {
        return Players.getLocal().getAnimation() == -1
                && Players.getLocal().getInteracting() == null
                && !Dialog.canContinue();
    }

    @Override
    public int execute() {
        if (Skills.getLevel(Skill.MAGIC) >= 45) {
            log.info("We have 45 magic, stopping");
            HOME_TELEPORT.get().interact(0);
            Sleep.sleep(15*1000, 20*1000);
            return -1;
        } else if (Skills.getLevel(Skill.ATTACK) >= 30 && Skills.getLevel(Skill.DEFENCE) >= 30) {
            log.info("We should kill cows"); //TODO: remove this log
            filter = (npc -> (npc.getName().equals("Cow") || npc.getName().equals("Cow calf")) && LUM_COWS.contains(npc));
        } else {
            log.info("We should kill seagulls"); //TODO: remove this log
            filter = npc -> npc.getName().equals("Seagull");
        }
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= runThreshold) {
            Movement.toggleRun();
            runThreshold = gaussian(1, 100, 30, 20);
            return gaussian(25, 15000, 200, 100);
        }
        if (Equipment.first("Iron longsword").isPresent()) {
            /** Set attack style to train shared */
            if (Varps.get(VarPlayer.ATTACK_STYLE.getId()) != 2) {
                log.info("Setting attack style to train shared");
                setAttackStyleToTrainShared();
                return Rand.nextInt(1000, 2000);
            }
        } else {
            /** Set attack style to train attack */
            if (Skills.getLevel(Skill.ATTACK) < 30 && Varps.get(VarPlayer.ATTACK_STYLE.getId()) != 0) {
                log.info("Setting attack style to train attack");
                setAttackStyleToTrainAttack();
                return Rand.nextInt(1000, 2000);
            }
            /** Set attack style to train defence*/
            if (Skills.getLevel(Skill.ATTACK) >= 30 && Skills.getLevel(Skill.DEFENCE) < 30 && Varps.get(VarPlayer.ATTACK_STYLE.getId()) != 3) {
                log.info("Setting attack style to train defence");
                setAttackStyleToTrainDefence();
                return Rand.nextInt(1000, 2000);
            }
        }
        /** hop to random world if too many people nearby */
        if (Players.all(player -> player.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) < 10).size() > 4) {
            log.info("Too many people around, hopping to random f2p world");
            hopToRandomF2PWorld();
        }
        NPC monster = NPCs.closest((npc) ->
                filter.test(npc)
                        && npc.getHealthRatio() != 0
                        && npc.getInteracting() == null
                        && !someoneElseIsAttacking(npc));
        if (monster.isPresent()) {
            monster.interact("Attack");
        }
        return gaussian(50, 15000, 1500, 1000);
    }

    private void setAttackStyleToTrainAttack() {
        log.info("Opening combat tab");
        OPEN_COMBAT_TAB.get().interact(0);
        Sleep.sleep(1000, 2000);
        if (SELECT_STAB_STYLE.get().isPresent()) {
            log.info("Selecting 'stab' attack style");
            SELECT_STAB_STYLE.get().interact(0);
            Sleep.sleep(1000, 2000);
        } else if (SELECT_CHOP_STYLE.get().isPresent()) {
            log.info("Selecting 'chop' attack style");
            SELECT_CHOP_STYLE.get().interact(0);
            Sleep.sleep(1000, 2000);
        }
    }

    private void setAttackStyleToTrainDefence() {
        log.info("Opening combat tab");
        OPEN_COMBAT_TAB.get().interact(0);
        Sleep.sleep(1000, 2000);
        if (SELECT_BLOCK_STYLE.get().isPresent()) {
            log.info("Selecting 'block' attack style");
            SELECT_BLOCK_STYLE.get().interact(0);
            Sleep.sleep(1000, 2000);
        }
    }

    private void setAttackStyleToTrainShared() {
        log.info("Opening combat tab");
        OPEN_COMBAT_TAB.get().interact(0);
        Sleep.sleep(1000, 2000);
        if (SELECT_LUNGE_STYLE.get().isPresent()) {
            log.info("Selecting 'lunge' attack style");
            SELECT_LUNGE_STYLE.get().interact(0);
            Sleep.sleep(1000, 2000);
        }
    }

    private void hopToRandomF2PWorld() {
        int initialWorldId = Worlds.getCurrentWorldId();
        World randomWorld = Worlds.getRandom(world -> !world.getTypes().contains(WorldType.MEMBERS) && !world.getTypes().contains(WorldType.PVP) && !world.getTypes().contains(WorldType.SKILL_TOTAL));
        Worlds.switchTo(randomWorld);
        Sleep.sleep(5 * 1000, 10 * 1000);
        if (Worlds.getCurrentWorld().getId() == initialWorldId) {
            log.info("World didn't switch, trying pressing 2");
            Keyboard.type('2');
            Sleep.sleep(5 * 1000, 10 * 1000);
        }
    }

    /**
     * Returns true if a player other than us is attacking the NPC
     *
     * @param npc
     * @return
     */
    private boolean someoneElseIsAttacking(NPC npc) {
        for (Player player : Players.all()) {
            if (player.equals(Players.getLocal())) {
                continue;
            }
            if (player.getInteracting() != null && player.getInteracting() instanceof Actor) {
                Actor theirTarget = (Actor) player.getInteracting();
                if (theirTarget != null && theirTarget.equals(npc)) {
                    log.info(player.getName() + " is attacking our target");
                    return true;
                }
            }
        }
        return false;
    }
}
