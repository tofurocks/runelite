package com.runemax.bot.skills.construction;

public enum ConstructionRoom {
    PARLOUR(1, 0),
    KITCHEN(5, 5000),
    DINING_ROOM(10, 5000),
    WORKSHOP(15, 10000);

    public final int levelRequirement;
    public final int price;

    ConstructionRoom(int levelRequirement, int price) {
        this.levelRequirement = levelRequirement;
        this.price = price;
    }
}
