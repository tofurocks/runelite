package com.runemax.bot.api.widget;

public class Production {
    public static final int WIDGET_GROUP = 270;

    public static boolean isAllSelected(){
        return !Widgets.get(WIDGET_GROUP, 12).hasAction("All");
    }

    public static void selectAll(){
        Widgets.get(WIDGET_GROUP, 12).interact("All");
    }

    public static void start(){
        Dialog.continueSpace();
    }

    public static boolean isOpen(){
        return Widgets.get(WIDGET_GROUP, 12).isPresent();
    }
}
