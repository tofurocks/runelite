package com.runemax.bot.scripts.util;

import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;

@Slf4j
public class DepositBox {
    /**
     * Items in our inventory when we have deposit box open are widgets (192, 2, <0-27>)
     *
     * @return
     */
    public static boolean isOpen() {
        Widget depositBoxWiget = Widgets.get(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER);
        return depositBoxWiget.isPresent() && !depositBoxWiget.isHidden();
    }

    private static final WidgetQuery depositInv = new WidgetQuery(WidgetID.DEPOSIT_BOX_GROUP_ID, w -> w.hasAction("Deposit inventory"));

    public static void depositInventory() {
        depositInv.get().interact("Deposit inventory");
    }

    public static void depositAll(String containsIgnoreCase) {
        if (!isOpen()) {
            return;
        }
        for (int i = 0; i < 27 & isOpen(); i++) {
            Widget itemWidget = Widgets.get(192, 2, i);
            if (itemWidget != null && itemWidget.getName().toLowerCase().contains(containsIgnoreCase.toLowerCase())) {
                log.info("Depositing all " + itemWidget.getName());
                itemWidget.interact(4);
                return;
            }
        }
    }
}
