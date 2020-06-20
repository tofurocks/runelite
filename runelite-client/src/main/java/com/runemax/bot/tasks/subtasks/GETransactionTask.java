package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.Constants;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.grandexchange.GrandExchange;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.widget.Widget;
import com.runemax.bot.interfaces.RequiresStartLocation;
import com.runemax.bot.tasks.framework.Task;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

import java.util.function.Predicate;

@Slf4j
abstract class GETransactionTask extends Task implements RequiresStartLocation {

    private static final Predicate<TileObject> BOOTH = to -> {
        if (!to.getName().equalsIgnoreCase("grand exchange booth")) return false;
        return to.hasAction("exchange");
    };

    public GETransactionTask(String taskDescription) {
        super(taskDescription);
    }

    public void openBooth() {
        Widget geWindow = Widgets.get(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER);
        if (geWindow.isEmpty() || geWindow.isHidden()) {
            TileObjects.closest(BOOTH).interact("exchange");
            Sleep.until(() -> geWindow.isPresent() && !geWindow.isHidden(), Rand.nextDuration(10, 15));
        }
    }

    public void collectItems() {
        if (GrandExchange.canCollect()) {
            log.info("Collecting stuff");
            GrandExchange.collect(true);
        }
    }

    public void createBuyOffer(int itemId, int quantity, float markupPercentage) {

        log.info("Buying {} x {} at {}% markup", quantity, itemId, markupPercentage);

        if (!GrandExchange.getView().equals(GrandExchange.View.BUYING)) {
            GrandExchange.createBuyOffer();
            Sleep.until(() -> GrandExchange.getView().equals(GrandExchange.View.BUYING), Rand.nextDuration(10, 15));
            Sleep.sleep(Rand.gaussian(25, 15000, 3000, 500));
        }

        if (!GrandExchange.getView().equals(GrandExchange.View.BUYING)) {
            log.error("Couldn't open GE BUY view, failed");
            return;
        }

        if (GrandExchange.getItemId() != itemId) {
            log.debug("Setting item");
            GrandExchange.setItem(itemId);
            Sleep.until(() -> GrandExchange.getItemId() == itemId, Rand.nextDuration(10, 15));
            Sleep.sleep(Rand.gaussian(25, 15000, 3000, 500));
        }

        if (markupPercentage != 0) {
            setPrice(markupPercentage);
        }

        if (quantity > 0) {
            setQuantity(quantity);
        } else {
            // set to sell all
        }

        GrandExchange.confirm();
    }

    public void createSellOffer(String itemName, int quantity, float markupPercentage) {
        log.info("Selling {} x {} at {}% markup", quantity, itemName, markupPercentage);

        if (!GrandExchange.getView().equals(GrandExchange.View.SELLING)) {
            log.info("Offering {}", itemName);
            GrandExchange.offer(item -> item.getName().equals(itemName));
            Sleep.until(() -> GrandExchange.getView().equals(GrandExchange.View.SELLING), Rand.nextDuration(10, 15));
            Sleep.sleep(Rand.gaussian(25, 15000, 3000, 50));
        }

        if (markupPercentage != 0) {
            setPrice(markupPercentage);
        }

        if (quantity > 0) {
            setQuantity(quantity);
        } else {
            // set to sell all
        }

        GrandExchange.confirm();
    }

    private void setPrice(float markupPercentage) {
        int finalPrice = (int) (GrandExchange.getPrice() * markupPercentage);
        if (GrandExchange.getPrice() != finalPrice) {
            log.info("Setting price to {}", finalPrice);
            GrandExchange.setPrice(finalPrice);
            Sleep.until(() -> GrandExchange.getPrice() == finalPrice, Rand.nextDuration(10, 15));
            Sleep.sleep(Rand.gaussian(25, 15000, 1000, 500));
        }
    }

    private void setQuantity(int quantity) {
        if (GrandExchange.getQuantity() != quantity) {
            log.info("Setting quantity to {}", quantity);
            GrandExchange.setQuantity(quantity);
            Sleep.until(() -> GrandExchange.getQuantity() == quantity, Rand.nextDuration(10, 15));
            Sleep.sleep(Rand.gaussian(25, 15000, 3000, 50));
        }
    }

    @Override
    public WorldPoint getStartLocation() {
        return Constants.GRAND_EXCHANGE.getRandomPoint();
    }
}
