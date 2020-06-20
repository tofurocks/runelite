package com.runemax.bot.scripts.combat.cows;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.tabs.magic.Spell;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.scripts.combat.CombatStore;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.WidgetID;

@Slf4j
public class EquipMagicGear extends Task {
    final WidgetQuery OPEN_COMBAT_TAB = new WidgetQuery(548, widget -> widget.hasAction("combat"));
    final WidgetQuery OPEN_SPELL_SELECT = new WidgetQuery(593, widget -> widget.hasAction("choose spell"));
    final WidgetQuery SELECT_WIND_STRIKE = new WidgetQuery(201, 1);

    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.ATTACK) < 30 || Skills.getLevel(Skill.DEFENCE) < 30){
            return false; //Not done training melee yet
        }
        if(Varps.get(VarPlayer.ATTACK_STYLE.getId()) == 4){
            return false; //We already have spell set
        }
        if ((Equipment.inSlot(EquipmentInventorySlot.WEAPON).isEmpty() || !Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air"))
                && Inventory.first("Staff of air").isPresent()) {
            return true;
        }
        if (Equipment.inSlot(EquipmentInventorySlot.WEAPON).isPresent() && Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air")
                && Varps.get(VarPlayer.ATTACK_STYLE.getId()) != 4) {
            log.info("We need to set autocast to wind strike");
            return true;
        }
        return false;
    }

    @Override
    public int execute() {
        if ((Equipment.inSlot(EquipmentInventorySlot.WEAPON).isEmpty() || !Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air"))
                && Inventory.first("Staff of air").isPresent()) {
            log.info("Equipping staff of air");
            Inventory.first("Staff of air").interact("Wield");
            return Rand.nextInt(1000, 2000);
        }
        log.info("Varp for attack style varplayer: " + Varps.get(VarPlayer.ATTACK_STYLE.getId()));
        if (Varps.get(VarPlayer.ATTACK_STYLE.getId()) != 4) {
            /** Set spell to air strike */
            log.info("Setting spell to wind strike");
            setAutocastSpellToWindStrike();
            return Rand.nextInt(1000, 2000);
        }
        return 0;
    }

    private void setAutocastSpellToWindStrike() {
        log.info("Opening combat tab");
        OPEN_COMBAT_TAB.get().interact(0);
        Sleep.sleep(1000, 2000);
        if(OPEN_SPELL_SELECT.get().isPresent()) {
            log.info("Opening spell select");
            OPEN_SPELL_SELECT.get().interact(0);
            Sleep.sleep(1000, 2000);
        }
        if(SELECT_WIND_STRIKE.get().isPresent()) {
            log.info("Selecting wind strike");
            SELECT_WIND_STRIKE.get().getChild(1).interact(0);
            Sleep.sleep(1000, 2000);
        }
    }
}
