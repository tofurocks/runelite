package com.runemax.bot.api.tabs.quest;

import lombok.Getter;

public enum Minigame {

    BARBARIAN_ASSAULT(1),
    BURTHORPE_GAMES_ROOM(2),
    BLAST_FURNACE(3),
    CASTLEWARS(4),
    CLAN_WARS(5),
    DAGANOTH_KINGS(6),
    FISHING_TRAWLER(7),
    GOD_WARS(8),
    LMS(9),
    NMZ(10),
    PEST_CONTROL(11),
    PLAYER_OWNED_HOUSES(12),
    RAT_PITS(13),
    SHADES_OF_MORTTON(14),
    SHIELD_OF_ARRAV(15),
    THEATRE_OF_BLOOD(16),
    TITHE_FARM(17),
    TROUBLE_BREWING(18),
    FIGHT_PIT(19),
    VOLCANIC_MINE(20);

    @Getter
    private int id;

    Minigame(int id) {
        this.id = id;
    }

}
