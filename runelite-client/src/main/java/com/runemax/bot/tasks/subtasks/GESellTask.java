package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.grandexchange.GrandExchange;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class GESellTask extends GETransactionTask {

    /**
     * <ItemName, Quantity>
     */
    private final HashMap<String, Integer> sellList;
    private final float markupPercentage;

    public GESellTask(String taskDescription, HashMap<String, Integer> sellList) {
        this(taskDescription, sellList, 0);
    }

    public GESellTask(String taskDescription, HashMap<String, Integer> sellList, float markupPercentage) {
        super(taskDescription);
        this.sellList = sellList;
        this.markupPercentage = markupPercentage;
    }

    @Override
    public Task.Result execute(TaskSet parentQueue) {

        // Open booth
        this.openBooth();

        if (sellList.size() > 7) {
            throw new BotException("Can't place more than 7 sell orders at once");
        }

        int freeSpaces = 7 - GrandExchange.getOffers().size();
        if (sellList.size() >= freeSpaces) {
            log.info("Not enough GE space to make all sales, waiting...");
            return Task.Result.RUNNING;
        }

        // Make orders
        for (String itemName : sellList.keySet()) {
            int quantity = sellList.get(itemName);
            this.createSellOffer(itemName, quantity, markupPercentage);
        }

        log.info("All GE sell offers placed");
        return Task.Result.SUCCESS;
    }

}
