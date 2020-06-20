package com.runemax.bot.skills.construction;

import java.util.HashMap;

public enum ConstructionObjective {
    CRUDE_WOODEN_CHAIR(1, ConstructionRoom.PARLOUR, new HashMap<>(){{put(ConstructionMaterial.WOODEN_PLANKS, 2);put(ConstructionMaterial.STEEL_NAILS, 2);}}),
    WOODEN_BOOKCASE(4, ConstructionRoom.PARLOUR, new HashMap<>(){{put(ConstructionMaterial.WOODEN_PLANKS, 4);put(ConstructionMaterial.STEEL_NAILS, 4);}}),
    WOODEN_LARDER(9, ConstructionRoom.KITCHEN, new HashMap<>(){{put(ConstructionMaterial.WOODEN_PLANKS, 8);put(ConstructionMaterial.STEEL_NAILS, 5);}}),
    REPAIR_BENCH(15, ConstructionRoom.WORKSHOP, new HashMap<>(){{put(ConstructionMaterial.OAK_PLANKS, 2);}}),
    CRAFTING_TABLE(16, ConstructionRoom.WORKSHOP, new HashMap<>(){{put(ConstructionMaterial.OAK_PLANKS, 4);}}),
    OAK_DINING_TABLE(22, ConstructionRoom.DINING_ROOM, new HashMap<>(){{put(ConstructionMaterial.OAK_PLANKS, 4);}}),
    CARVED_OAK_TABLE(31, ConstructionRoom.DINING_ROOM, new HashMap<>(){{put(ConstructionMaterial.OAK_PLANKS, 6);}}),
    OAK_LARDER(33, ConstructionRoom.DINING_ROOM, new HashMap<>(){{put(ConstructionMaterial.OAK_PLANKS, 8);}});

    public final int levelRequired;
    public final ConstructionRoom room;
    public final HashMap<ConstructionMaterial, Integer> materials;

    ConstructionObjective(int levelRequired, ConstructionRoom room, HashMap<ConstructionMaterial, Integer> materials) {
        this.levelRequired = levelRequired;
        this.room = room;
        this.materials = materials;
    }

    public static ConstructionObjective getConstructionObjective(int constructionLevel) {
        for(ConstructionObjective objective : ConstructionObjective.values()) {
            if(objective.levelRequired <= constructionLevel) {
                return objective;
            }
        }
        return null;
    }
}
