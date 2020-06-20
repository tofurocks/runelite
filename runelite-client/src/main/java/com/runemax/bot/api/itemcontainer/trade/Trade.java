package com.runemax.bot.api.itemcontainer.trade;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.input.Keyboard;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;
import net.runelite.api.widgets.WidgetID;
import com.runemax.bot.api.exception.BotException;

import javax.annotation.Nonnull;

public class Trade {
    public static final int SECOND_SCREEN_GROUP_ID = 334;

    public enum View{
        CLOSED, FIRST, SECOND
        ;
    }

    public static int getOfferedQuantity(int itemId) {
        Widget ourOfferedItems = Widgets.get(335, 25);
        for (Widget item : ourOfferedItems.getChildren()) {
            int widgItemId = item.getItemId();
            if (widgItemId == -1) continue;
            if (widgItemId == itemId) {
                return item.getItemQuantity();
            }
        }
        return 0;
    }

    @Nonnull
    public static View getView(){
        Widget first = Widgets.get(WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID, 10);
        if(first.isPresent() && !first.isHidden()){
            if (!first.hasAction("Accept")) throw new BotException("it hink widg brok");

            return View.FIRST;
        }

        Widget second = Widgets.get(SECOND_SCREEN_GROUP_ID, 13);
        if(second.isPresent() && !second.isHidden()){
            if (!second.hasAction("Accept")) throw new BotException("it hink widg brok");
            return View.SECOND;
        }

        return View.CLOSED;
    }

    public static void offerAll(int itemId){
        Widget tradeInv = Widgets.get(WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID, 0);
        for (Widget item : tradeInv.getChildren()) {
            int widgItemId = item.getItemId();

            if(widgItemId == -1) continue;

            if(widgItemId == itemId){
                item.interact("offer-all");
                return;
            }
        }

        throw new BotException("no item found");
    }

    public static void offer(int itemId, int quantity) {
        Widget tradeInv = Widgets.get(WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID, 0);
        for (Widget item : tradeInv.getChildren()) {
            int widgItemId = item.getItemId();

            if (widgItemId == -1) continue;

            if (widgItemId == itemId) {
                item.interact("offer-x");
                Sleep.until(Dialog::isEnterAmountOpen, Rand.nextInt(10*1000, 15*1000));
                Dialog.enterAmount(quantity);
                Sleep.sleep(Rand.gaussian(200, 15*1000, 1000, 500));
                Keyboard.enter();
                Sleep.sleep(Rand.gaussian(200, 15*1000, 1000, 500));
                return;
            }
        }

        throw new BotException("no item found");
    }

    public static void accept(boolean first){
        Widgets.get(first ? WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID : SECOND_SCREEN_GROUP_ID, first ? 10 : 13).interact("Accept");
    }

    public static void accept(){
        View view = getView();
        if(view == View.CLOSED){
            throw new BotException("closed");
        }

        accept(view == View.FIRST);
    }
}
