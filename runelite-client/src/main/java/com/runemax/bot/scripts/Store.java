package com.runemax.bot.scripts;

import com.rabbitmq.client.Channel;
import com.sun.jersey.api.client.Client;
import lombok.Getter;
import lombok.Setter;

public class Store {
    @Setter
    @Getter
    private static Channel channel;
    @Setter
    @Getter
    private static Client jerseyClient;
    @Setter
    @Getter
    private static boolean butlerPurchased = false;
    @Setter
    @Getter
    private static boolean plankReloadNeeded = false;
    @Setter
    @Getter
    private static String job;
    @Setter
    @Getter
    private static String plankLogName;
    @Setter
    @Getter
    private static String plankName;
    @Setter
    @Getter
    private static boolean running = false;
    @Setter
    @Getter
    private static boolean isSpidineBugged = false;
    @Setter
    @Getter
    private static boolean isButlerBugged = false;
    @Setter
    @Getter
    private static long lastXpSystemTime;
    @Setter
    @Getter
    private static boolean sessionExpired = false;
    @Setter
    @Getter
    private static long breakEndTime = 0;
    @Getter
    @Setter
    private static int painThreshold;
    @Getter
    @Setter
    private static int lootBankedCount;
    @Getter
    @Setter
    private static int initialLootBankedCount;

}
