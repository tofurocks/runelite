package com.runemax.bot.scripts;

import java.util.Random;

import com.runemax.bot.api.commons.By;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.data.VarpData;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.Game;
import com.runemax.bot.api.input.Keyboard;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.entity.Entity;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuOpcode;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.inventory.InventoryItem;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.tabs.Tab;
import com.runemax.bot.api.tabs.Tabs;
import com.runemax.bot.api.varps.Varps;
import net.runelite.api.widgets.WidgetInfo;

import java.util.function.Predicate;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.api.commons.Sleep.sleep;

@ScriptMeta("Tutorial")
@Slf4j
public class Tutorial extends BotScript {
    private final WidgetQuery SMITH_BRONZE_DAGGER = new WidgetQuery(WidgetID.SMITHING_GROUP_ID, w -> w.getName().toLowerCase().contains("bronze dagger"));
    private final WidgetQuery WIND_STRIKE = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Wind Strike"));

    @Override
    public void loop() {
        int delay = this.execute();
        Sleep.sleep(delay);
    }

    public int execute() {
        int config = Varps.get(VarpData.TUT_PROGRESS);
        log.info("progress: " + config);
        boolean doDefault = false;
        Predicate<String> defaultAction = a -> true;

        Player local = Players.getLocal();
        if (local.getAnimation() != -1) return 1000;

        if (config != 1 && Dialog.isLegacyContinue()) {
            Dialog.continueLegacy();
            return gaussian(25, 15000, 200, 125);
        }

        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return gaussian(25, 15000, 200, 125);
        }

        switch (config) {
            case 0:
                break;
            case 1:
                Widget nameStatus = Widgets.get(558, 12);
                if (nameStatus.isPresent()) {
                  /*  if (Widgets.get(558, 14).getText().toLowerCase().contains("loading")) {
                        Widgets.get(558, 14).interact("set name");
                        sleep(gaussian(15000, 120000, 75000, 30000));
                        break;
                    }*/

                    String statusText = nameStatus.getText().toLowerCase();
                    System.out.println(statusText);
                    if (statusText.contains("great! this display name")) {
                        Widgets.get(558, 18).interact("set name");
                        break;
                    }

                    if (!Dialog.isEnterAmountOpen()) {
                        Widgets.get(558, 17).interact("Look up name");
                        break;
                    }

                    String user = NameGenerator.generateName().toLowerCase();
                    char[] chars = user.toCharArray();

                    if (statusText.contains("sorry, this display name is <col=ff0000>not available</col>.<br>try clicking one of our suggestions, instead")) {
                        user = NameGenerator.generateName().toLowerCase();
                        break;
                    }else if (!statusText.contains("please enter a name to see")) {
                        break;
                    }


                    Keyboard.type(user, true);
                    break;
                }

                Widgets.get(269, 99).interact(0);
                break;
            case 2:
                doDefault = true;
                break;
            case 3:
                if (Game.isFixedMode()) {
                    Tabs.open(Tab.OPTIONS);
                    break;
                }

                Widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_OPTIONS_ICON).interact("Options");

                break;
            case 7:
                Widget fixedMode = Widgets.get(261, 33);
                if (!Game.isFixedMode()) {
                    fixedMode.interact("Fixed mode");
                    break;
                }

                String soundComponentName = null;
                if (Varps.get(168) != 4) {
                    soundComponentName = "adjust music";
                } else if (Varps.get(169) != 4) {
                    soundComponentName = "adjust sound";
                } else if (Varps.get(872) != 4) {
                    soundComponentName = "adjust area";
                }

                if (soundComponentName != null) {
                    String finalSoundComponentName = soundComponentName;
                    new WidgetQuery(261, w -> w.hasAction(s -> s.toLowerCase().contains(finalSoundComponentName))).get().interact(a -> true);
                    break;
                }

                doDefault = true;
                break;
            case 30:
                Tabs.open(Tab.INVENTORY);
                break;
            case 50:
                Tabs.open(Tab.SKILLS);
                break;
            case 80:
                Inventory.useOn(By.name("Tinderbox"), Inventory.first("Logs"));
                break;
            case 90:
                Inventory.useOn(By.name("Raw shrimps"), TileObjects.closest("Fire"));
                break;
            case 150:
                Inventory.useOn(By.name("Pot of flour"), Inventory.first("Bucket of water"));
                break;
            case 230:
                Tabs.open(Tab.QUESTS);
                break;
            case 260:
                if (NPCs.getHintArrowed().isEmpty()) {
                    Movement.walk(3080, 9503);
                    break;
                }

                doDefault = true;
                break;
            case 350:
                Widget smithDagger = SMITH_BRONZE_DAGGER.get();
                if (smithDagger.isEmpty()) {
                    TileObjects.closest("Anvil").interact("Smith");
                    break;
                }

                smithDagger.interact("Smith");
                Sleep.sleep(1000);
                break;
            case 390:
                Tabs.open(Tab.EQUIPMENT);
                break;
            case 400:

                Widgets.get(WidgetID.EQUIPMENT_GROUP_ID, 1).interact(a -> true);
                break;
            case 405:
                Inventory.first("Bronze dagger").interact("Wield");
                break;
            case 420:
                InventoryItem sword = Inventory.first("Bronze sword");
                if (sword.isPresent()) {
                    sword.interact("Wield");
                    break;
                }

                Inventory.first("Wooden Shield").interact("Wield");
                break;
            case 430:
                Tabs.open(Tab.COMBAT);
                break;
            case 440:
                TileObjects.getFirstAt(new WorldPoint(3111, 9518, Client.getPlane()), By.name("Gate")).interact("Open");
                break;
            case 470:
                TileObjects.getFirstAt(new WorldPoint(3111, 9518, 0), o -> o.hasAction("Open")).interact("open");
                Sleep.sleep(Rand.nextInt(10, 15) * 1000);

                NPCs.getHintArrowed().interact("Talk-to");
                Sleep.sleep(Rand.nextInt(10, 15) * 1000);

                doDefault = true;
                break;
            case 490:
                InventoryItem bow = Inventory.first("Shortbow");
                if (bow.isPresent()) {
                    bow.interact("Wield");
                    break;
                }

                InventoryItem arrows = Inventory.first("Bronze arrow");
                if (arrows.isPresent()) {
                    arrows.interact("Wield");
                    break;
                }

                if (NPCs.getHintArrowed().isEmpty()) {
                    NPCs.closest("Giant rat").interact("Attack");
                    break;
                }

                doDefault = true;
                break;
            case 520:
                LocalPoint hintPoint520 = LocalPoint.fromWorld(Client.getInstance(), new WorldPoint(3119, 3121, Client.getPlane()));
                Interact.interact("", "", 26815, MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), hintPoint520.getSceneX(), hintPoint520.getSceneY());
                break;
            case 531:
                Tabs.open(Tab.ACCOUNT);
                break;
            case 550:
                if (Players.getLocal().getY() > 3116) {
                    Movement.walk(3134, 3116);
                    break;
                }

                doDefault = true;
                break;
            case 560:
                if (Dialog.canContinue()) {
                    doDefault = true;
                    break;
                }

                Tabs.open(Tab.PRAYER);
                break;
            case 580:
                if (Dialog.canContinue()) {
                    doDefault = true;
                    break;
                }

                Tabs.open(Tab.FRIENDS);
                break;
            case 620:
                if (Players.getLocal().getY() > 3100) {
                    Movement.walk(3131, 3088);
                    break;
                }

                doDefault = true;
                break;
            case 630:
                Tabs.open(Tab.MAGIC);
                break;
            case 650:
                WorldPoint strikeLoc = new WorldPoint(3139, 3091, Client.getPlane());
                if (strikeLoc.equals(Players.getLocal().getWorldLocation())) {
                    WIND_STRIKE.get().interact(0);
                    Sleep.until(() -> Interact.isReady() && WIND_STRIKE.get().getBorderType() == 2, 500);
                    NPCs.closest("Chicken").interactSpellOn();
                    break;
                }

                Movement.walk(strikeLoc);
                break;
            case 670:
                if (Dialog.isViewingOptions()) {
                    Dialog.chooseOption("Yes.", "No,");
                    break;
                }

                doDefault = true;
                break;
            case 1000:
                log.info("Handling Adventurer Jon");
                if(Equipment.first("Iron sword").isPresent()){
                    this.stopLooping();
                }
                InventoryItem kit = Inventory.first("Combat path starter kit");
                if (kit.isPresent()) {
                    log.info("We have a starter kit, opening it and ending script");
                    kit.interact("Search");
                    Sleep.sleep(gaussian(1000, 15000, 2000, 750));
                    Inventory.first("Wizard hat").interact("Wear");
                    Sleep.sleep(gaussian(1000, 15000, 2000, 750));
                    Inventory.first("Black skirt").interact("Wear");
                    Sleep.sleep(gaussian(1000, 15000, 2000, 750));
                    Inventory.first("Black robe").interact("Wear");
                    Sleep.sleep(gaussian(1000, 15000, 2000, 750));
                    Inventory.first("Leather vambraces").interact("Wear");
                    Sleep.sleep(gaussian(1000, 15000, 2000, 750));
                    Inventory.first("Iron longsword").interact("Wield");
                    this.stopLooping();
                }
                if (Dialog.isViewingOptions() && Dialog.hasOption("starter kits for me")) {
                    Dialog.chooseOption("starter kits for me");
                    return gaussian(25, 15000, 200, 125);
                }
                NPCs.closest("Adventurer Jon").interact("Talk-to");
                return gaussian(25, 15000, 1200, 750);
            default:
                doDefault = true;
                break;
        }

        if (!doDefault) return 0;

        if (Dialog.isViewingOptions()) {
            Dialog.chooseOption(0);
            return gaussian(25, 15000, 200, 125);
        }

        Entity hinted = null;
        switch (Client.getHintArrowType()) {
            case NPC:
                hinted = NPCs.getHintArrowed();
                break;
            case WORLD_POSITION:
                for (TileObject obj : TileObjects.getAt(Client.getHintArrowPoint())) {
                    if (obj.hasAction(defaultAction)) {
                        hinted = obj;
                        break;
                    }
                }
                break;
        }

        if (hinted == null) {
            log.info("cant find hinted");
            return 0;
        }
        hinted.interact(defaultAction);
        return gaussian(25, 15000, 200, 125);
    }
}


class NameGenerator {

    private static final String[] Beginning = { "air", "ir", "mi", "sor", "mee", "clo",
        "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
        "marac", "zoir", "slamar", "salmar", "urak", "de", "ad","d", "ed", "ark", "arc", "es", "er", "der","Kr", "Ca", "Ra", "Mrok", "Cru",
        "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
        "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
        "Mar", "Luk", "Di", "Do", "Ra", "Leo", "De"  };
    private static final String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
        "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
        "marac", "zoir", "slamar", "salmar", "urak", "de", "ad","d", "ed", "ark", "arc", "es", "er", "der",
        "tron", "med", "ure", "zur", "cred", "mur", "Kr", "Ca", "Ra", "Mrok", "Cru",
        "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
        "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro"};
    private static final String[] End = {"air", "ir", "mi", "sor", "mee", "clo",
        "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
        "marac", "zoir", "slamar", "salmar", "urak", "de", "ad","d", "ed", "ark", "arc", "es", "er", "der", "d", "ed", "ark", "arc", "es", "er", "der",
        "tron", "med", "ure", "zur", "cred", "mur","Kr", "Ca", "Ra", "Mrok", "Cru",
        "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
        "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro", };

    private static final Random rand = new Random();

    public static String generateName() {

        return rand.nextInt(100) + Beginning[rand.nextInt(Beginning.length)] +
            Middle[rand.nextInt(Middle.length)]+
            End[rand.nextInt(End.length)] + rand.nextInt(99);

    }

}
