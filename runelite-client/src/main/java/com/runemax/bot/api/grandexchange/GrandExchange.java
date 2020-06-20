package com.runemax.bot.api.grandexchange;

import com.runemax.bot.api.account.Account;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.inventory.InventoryItem;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.varps.Varps;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class GrandExchange {
    public static final Predicate<TileObject> BOOTH = to -> {
        if (!to.getName().equalsIgnoreCase("grand exchange booth")) return false;
        return to.hasAction("exchange");
    };

    public enum View {
        CLOSED,
        OFFERS,
        BUYING,
        SELLING,
        UNKNOWN,
        ;
    }

    @Nonnull
    public static View getView() {
        Widget geWindow = Widgets.get(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER);
        if (geWindow.isEmpty() || geWindow.isHidden()) {
            return View.CLOSED;
        }

        Widget offersContainer = Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 7);
        if (offersContainer.isPresent() && !offersContainer.isHidden()) {
            return View.OFFERS;
        }

        String text = Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER).getChild(18).getText();
        if (text == null) {
            log.warn("i thinkwidget broke, text null");
            return View.UNKNOWN;
        } else if (text.equals("Sell offer")) {
            return View.SELLING;
        } else if (text.equals("Buy offer")) {
            return View.BUYING;
        } else {
            log.warn("i think a widget broke weird text: " + text);
            return View.UNKNOWN;
        }
    }

    public static int getItemId() {
        return Varps.get(VarPlayer.CURRENT_GE_ITEM.getId());
    }

    public static void setItem(int id) {
        Client.runScript(754, id, 84);
    }

    public static int getPrice() {
        return Varps.getBit(4398);
    }

    private static final WidgetQuery enterPrice = new WidgetQuery(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER.getGroupId(), WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER.getChildId()).withGrandChild(w-> w.hasAction("Enter price"));
    public static void enterPrice() {
        enterPrice.get().interact("Enter price");
    }

    public static void setPrice(int price) {
        enterPrice();
        Dialog.enterAmount(price);
    }

    public static int getQuantity() {
        return Varps.getBit(4396);
    }

    private static final WidgetQuery enterQuantity = new WidgetQuery(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER.getGroupId(), WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER.getChildId()).withGrandChild(w-> w.hasAction("Enter quantity"));
    public static void setQuantity(int quantity) {
        enterQuantity.get().interact("Enter quantity");
        Dialog.enterAmount(quantity);
    }

    public static int getGuidePrice() {
        return Integer.parseInt(Widgets.get(WidgetInfo.GRAND_EXCHANGE_OFFER_PRICE).getText().replaceAll("[^0-9]", ""));
    }

    public static void openBooth() {
        TileObjects.closest(BOOTH).interact("exchange");
    }

    public static void offer(Predicate<? super InventoryItem> predicate) {
        InventoryItem first = Inventory.first(predicate);

        Interact.interact(first.getMenuTarget(), "Offer", 1, 57, first.getIndex(), 30605312);
    }

    public static void createBuyOffer() {
        Widget[] group = Widgets.getGroup(WidgetID.GRAND_EXCHANGE_GROUP_ID);

        for (int i = 0; i < group.length; i++) {
            if (i < 7) continue;

            Widget child = group[i].getChild(3);
            if (child.hasAction("Create <col=ff9040>Buy</col> offer")) {
                child.interact("buy");
                break;
            }
        }

        throw new BotException("no free slot");
    }

    public static ArrayList<GrandExchangeOffer> getOffers() {
        return new ArrayList<>(Arrays.asList(Client.getGrandExchangeOffers()));
    }

    public static boolean haveEmptySlot() {
        ArrayList<GrandExchangeOffer> offers = getOffers();
        for (int i = 0; i < offers.size(); i++) {
            if (i == 3 && Account.getMembership() == 0) return false;

            if (offers.get(i).getState() == GrandExchangeOfferState.EMPTY) return true;
        }

        return false;
    }

    private static final List<GrandExchangeOfferState> COMPLETED_STATES = Arrays.asList(
            GrandExchangeOfferState.BOUGHT,
            GrandExchangeOfferState.SOLD,
            GrandExchangeOfferState.CANCELLED_BUY,
            GrandExchangeOfferState.CANCELLED_SELL
    );

    public static boolean canCollect() {
        for (GrandExchangeOffer offer : getOffers()) {
            if (COMPLETED_STATES.contains(offer.getState())) return true;
        }

        return false;
    }

    public static void collect(boolean toBank) {
        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 6, 0).interact(toBank ? "Collect to bank" : "Collect to inventory");
    }

    public static void openBank() {
        TileObjects.closest(to -> to.getName().equalsIgnoreCase("grand exchange booth") && to.hasAction("bank")).interact("bank");
    }

    public static void confirm() {
        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 27).interact("Confirm");
    }

    public static void abortOffer(int slot) {
        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, slot + 7).getChild(2).interact("abort offer");
    }
}
