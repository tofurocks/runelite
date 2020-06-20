package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.grandexchange.GrandExchange;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class GEPurchaseTask extends GETransactionTask {

    /**
     * <ItemId, Quantity>
     */
    private HashMap<Integer, Integer> shoppingList;
    private final float markupPercentage;

    public GEPurchaseTask(String taskDescription, HashMap<Integer, Integer> shoppingList, float markupPercent) {
        super(taskDescription);
        this.shoppingList = shoppingList;
        this.markupPercentage = markupPercent;
    }

    @Override
    public Task.Result execute(TaskSet parentQueue) {

        // Open booth
        this.openBooth();

        if (shoppingList.size() > 7) {
            throw new BotException("Can't place more than 7 buy orders at once");
        }

        int freeSpaces = 7 - GrandExchange.getOffers().size();
        if (shoppingList.size() >= freeSpaces) {
            log.info("Not enough GE space to make all purchases, waiting...");
            return Task.Result.RUNNING;
        }

        // Make orders
        for (int itemId : shoppingList.keySet()) {
            int quantity = shoppingList.get(itemId);
            this.createBuyOffer(itemId, quantity, markupPercentage); // buy with up to 10% markup
        }

        log.info("All GE buy offers placed");
        return Task.Result.SUCCESS;
    }
}
