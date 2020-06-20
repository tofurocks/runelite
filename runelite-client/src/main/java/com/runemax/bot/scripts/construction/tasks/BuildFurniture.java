package com.runemax.bot.scripts.construction.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.widget.Widget;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.scripts.construction.util.ConstructionUtil.COMPLETED_FURNITURE_NAMES;

@Slf4j
public class BuildFurniture extends Task {
    String[] PLANKS = {"Plank", "Oak plank"};
    int BELL_PULL = 13308;
    int BUILT_BED = 13149;

    @Override
    public boolean activate() {
        if (TileObjects.closest(COMPLETED_FURNITURE_NAMES).isPresent()) {
            log.info("Completed furniture already present");
            return false; //We should remove our completed furniture first
        }
        Widget conWidget = Widgets.get(458, 4);
        if (conWidget.isPresent() && !conWidget.isHidden()) {
            log.info("Construction widget already open");
            return false; //We should finish with our open interface first
        }
        int neededPlanks = 8;
        if (Skills.getLevel(Skill.CONSTRUCTION) < 16) {
            neededPlanks = 2;
            log.info("We should use regular planks and we have " + Inventory.count(item -> !item.isNoted() && item.getName().toLowerCase().equals("plank")));
            return Inventory.count(item -> !item.isNoted() && item.getName().toLowerCase().equals("plank")) >= neededPlanks;
        } else if (Skills.getLevel(Skill.CONSTRUCTION) < 33) {
            neededPlanks = 4;
        } else if (Skills.getLevel(Skill.CONSTRUCTION) == 50) {
            /** Hacky, bell pull object isn't findable for some reason so instead check what spaces are available
             * If bell pull space is present, we need to build a bell pull
             * If seating space is not present, we need to build a dining room*/
            if (TileObjects.closest("Bell pull space").isPresent()
            || TileObjects.closest("Seating space").isEmpty()) {
                log.info("Bell-pull is not built yet");
                log.info("bell pull space is present? " + TileObjects.closest("Bell pull space").isPresent());
                log.info("Seating space is empty? " + TileObjects.closest("Seating space").isEmpty());
                return Inventory.first(obj -> obj.getQuantity() == 1 && obj.getName().toLowerCase().equals("teak plank")).isPresent()
                        && Inventory.first("Bolt of cloth").isPresent();
            }
            if (TileObjects.all(obj -> obj.getId() == BUILT_BED).size() < 8) { //Each bed takes 4 tiles so 2 beds = 8 objects
                log.info("Beds are not built yet");
                return Inventory.count(item -> item.getQuantity() == 1 && item.getName().toLowerCase().equals("oak plank")) >= 2
                        && Inventory.first("Bolt of cloth").isPresent();
            }
            log.info("We are done building everything we need in our POH!");
            return false; //Done building everything we need in our POH
        }
        return Inventory.count(item -> !item.isNoted() && item.getName().toLowerCase().equals("oak plank")) >= neededPlanks;
    }

    @Override
    public int execute() {
        if (Dialog.isViewingOptions()) {
            Dialog.chooseOption("Build");
        } else if (Skills.getLevel(Skill.CONSTRUCTION) < 16) {
            buildChair();
        } else if (Skills.getLevel(Skill.CONSTRUCTION) < 33) {
            buildCraftingTable();
        } else if (Skills.getLevel(Skill.CONSTRUCTION) < 50) {
            buildLarder();
        } else if (TileObjects.closest(BELL_PULL).isEmpty()) {
            buildBellPull();
        } else if (TileObjects.all(obj -> obj.getId() == BUILT_BED).size() < 8) {
            buildBeds();
        }
        return gaussian(50, 15000, 200, 100);
    }

    private void buildBeds() {
        log.info("Building oak beds");
        TileObject bedSpace = TileObjects.closest("Bed space");
        if (bedSpace.isEmpty()) {
            log.info("No bed space, building bedroom");
            buildBedroom();
            Sleep.sleep(gaussian(50, 15000, 200, 100));
            return;
        }
        log.info("Interacting bed space");
        bedSpace.interact("Build");
        Sleep.until(() -> Widgets.get(458, 4).isPresent() && !Widgets.get(458, 4).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
    }

    private void buildBellPull() {
        TileObject bellPull = TileObjects.closest(BELL_PULL);
        if (bellPull.isPresent()) {
            log.info("Bell pull is already built");
            return;
        }
        TileObject bellSpace = TileObjects.closest("Bell pull space");
        if (bellSpace.isEmpty()) {
            log.info("No bell pull space, building dining room");
            buildDiningRoom();
            Sleep.sleep(gaussian(50, 15000, 200, 100));
            return;
        }
        log.info("Interacting bell pull space");
        bellSpace.interact("Build");
        Sleep.until(() -> Widgets.get(458, 4).isPresent() && !Widgets.get(458, 4).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
    }

    private void buildDiningRoom() {
        log.info("Building dining room");
        if (!Widgets.get(212, 23).isPresent()) {
            /** Use the south-east-most door hotspot */
            List<TileObject> doorHotspots = TileObjects.all().stream().filter(item -> item.getId() == 15313).collect(Collectors.toList());
            doorHotspots.sort((i1, i2) -> {
                if (i1.getWorldLocation().getX() != i2.getWorldLocation().getX()) {
                    return i2.getWorldLocation().getX() - i1.getWorldLocation().getX();
                }
                return i1.getWorldLocation().getY() - i2.getWorldLocation().getY();
            });
            TileObject doorHotspot = doorHotspots.get(0);
            if (doorHotspot.isPresent()) {
                log.info("Interacting with sorted (east) door hotspot with ID " + doorHotspot.getId() + " and name " + doorHotspot.getName());
                doorHotspot.interact("Build");
                Sleep.until(() -> Widgets.get(212, 23).isPresent() && !Widgets.get(212, 23).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
            }
            return;
        }
        log.info("Clicking build dining room widget");
        final WidgetQuery BUILD_DINING_ROOM = new WidgetQuery(212, widget -> widget.hasAction("dining"));
        BUILD_DINING_ROOM.get().interact(0);
        Sleep.until(Dialog::isOpen, Rand.nextInt(10 * 1000, 20 * 1000));
        return;
    }

    private void buildBedroom() {
        log.info("Building bedroom");
        if (!Widgets.get(212, 23).isPresent()) {
            /** Use the southern-most door hotspot */
            List<TileObject> doorHotspots = TileObjects.all().stream().filter(item -> item.getId() == 15313).collect(Collectors.toList());
            doorHotspots.sort(Comparator.comparingInt(i -> i.getWorldLocation().getY()));
            TileObject doorHotspot = doorHotspots.get(0);
            if (doorHotspot.isPresent()) {
                log.info("Interacting with sorted (south) door hotspot with ID " + doorHotspot.getId() + " and name " + doorHotspot.getName());
                doorHotspot.interact("Build");
                Sleep.until(() -> Widgets.get(212, 23).isPresent() && !Widgets.get(212, 23).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
            }
            return;
        }
        log.info("Clicking build bedroom widget");
        final WidgetQuery BUILD_DINING_ROOM = new WidgetQuery(212, widget -> widget.hasAction("bedroom"));
        BUILD_DINING_ROOM.get().interact(0);
        Sleep.until(Dialog::isOpen, Rand.nextInt(10 * 1000, 20 * 1000));
        return;
    }

    private void buildLarder() {
        TileObject larderSpace = TileObjects.closest("Larder space");
        if (!larderSpace.isPresent()) {
            log.info("No larder space, building kitchen");
            buildKitchen();
            Sleep.sleep(gaussian(50, 15000, 200, 100));
            return;
        }
        larderSpace.interact("Build");
        Sleep.until(() -> Widgets.get(458, 4).isPresent() && !Widgets.get(458, 4).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
    }

    private void buildCraftingTable() {
        TileObject craftingTableSpace = TileObjects.closest("Clockmaking space");
        if (!craftingTableSpace.isPresent()) {
            log.info("No clock making space, building workshop");
            buildWorkshop();
            Sleep.sleep(gaussian(50, 15000, 200, 100));
            return;
        }
        craftingTableSpace.interact("Build");
        Sleep.until(() -> Widgets.get(458, 4).isPresent() && !Widgets.get(458, 4).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
    }

    private void buildChair() {
        TileObject chairSpace = TileObjects.closest("Chair space");
        if (!chairSpace.isPresent()) {
            Sleep.sleep(gaussian(50, 15000, 200, 100));
            return;
        }
        chairSpace.interact("Build");
        Sleep.until(() -> Widgets.get(458, 4).isPresent() && !Widgets.get(458, 4).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
    }

    private void buildKitchen() {
        if (!Widgets.get(212, 23).isPresent()) {
            /** Use the door hotspot east of the portal */
            List<TileObject> doorHotspots = TileObjects.all().stream().filter(item -> item.getId() == 15313).collect(Collectors.toList());
            doorHotspots.sort((i1, i2) -> {
                if (i1.getWorldLocation().getX() != i2.getWorldLocation().getX()) {
                    return i2.getWorldLocation().getX() - i1.getWorldLocation().getX();
                }
                return i1.getWorldLocation().getY() - i2.getWorldLocation().getY();
            });
            TileObject doorHotspot = doorHotspots.get(0);
            if (doorHotspot.isPresent()) {
                log.info("Interacting with sorted (east) door hotspot with ID " + doorHotspot.getId() + " and name " + doorHotspot.getName());
                doorHotspot.interact("Build");
                Sleep.until(() -> Widgets.get(212, 23).isPresent() && !Widgets.get(212, 23).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
            }
            return;
        }
        log.info("Clicking build kitchen widget");
        final WidgetQuery BUILD_KITCHEN = new WidgetQuery(212, widget -> widget.hasAction("kitchen"));
        BUILD_KITCHEN.get().interact(0);
        Sleep.until(Dialog::isOpen, Rand.nextInt(10 * 1000, 20 * 1000));
        return;
    }

    private void buildWorkshop() {
        if (!Widgets.get(212, 23).isPresent()) {
            /** Use the door hotspot west of the portal */
            List<TileObject> doorHotspots = TileObjects.all(obj -> obj.getName().equalsIgnoreCase("door hotspot"));
            doorHotspots.sort(Comparator.comparingInt(i -> i.getWorldLocation().getX()));
            TileObject doorHotspot = doorHotspots.get(0);
            if (doorHotspot.isPresent()) {
                log.info("Interacting with sorted (west) door hotspot");
                doorHotspot.interact("Build");
                Sleep.until(() -> Widgets.get(212, 23).isPresent() && !Widgets.get(212, 23).isHidden(), Rand.nextInt(10 * 1000, 15 * 1000));
            }
            return;
        }
        log.info("Clicking build workshop widget");
        final WidgetQuery BUILD_WORKSHOP = new WidgetQuery(212, widget -> widget.hasAction("workshop"));
        BUILD_WORKSHOP.get().interact(0);
        Sleep.until(Dialog::isOpen, Rand.nextInt(10 * 1000, 20 * 1000));
        return;
    }
}
