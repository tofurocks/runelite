package com.runemax.bot.api.wrappers.widget;

import lombok.experimental.Delegate;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.WidgetType;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.wrappers.Interactable;
import com.runemax.bot.api.wrappers.RlWrapper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Widget extends RlWrapper<net.runelite.api.widgets.Widget> implements net.runelite.api.widgets.Widget, Interactable {
    @Nonnull
    public static Widget[] wrap(net.runelite.api.widgets.Widget[] raw) {
        if (raw == null) return new Widget[0];
        Widget[] wrapped = new Widget[raw.length];
        for (int i = 0; i < raw.length; i++) {
            wrapped[i] = new Widget(raw[i]);
        }

        return wrapped;
    }

    private interface Excludes {
        net.runelite.api.widgets.Widget getParent();

        net.runelite.api.widgets.Widget getChild(int index);

        net.runelite.api.widgets.Widget[] getChildren();

        boolean isHidden();
    }

    public Widget(net.runelite.api.widgets.Widget rl) {
        super(rl);
    }

    @Delegate(excludes = Excludes.class)
    @Override
    @Nonnull
    public net.runelite.api.widgets.Widget getRl() {
        return super.getRl();
    }

    @Override
    @Nonnull
    public Widget getParent() {
        return new Widget(getRl().getParent());
    }

    @Override
    @Nonnull
    public Widget getChild(int index) {
        return new Widget(getRl().getChild(index));
    }

    @Override
    @Nonnull
    public Widget[] getChildren() {
        return Widget.wrap(getRl().getChildren());
    }

    @Override
    public boolean isHidden() {
        net.runelite.api.widgets.Widget rl = getRl();
        return OnGameThread.invokeAndWait(rl::isHidden);
    }

    @Override
    public int getMenuIdentifier(Integer actionIndex) {
        switch (getType()) {
            case WidgetType.LAYER:
            case WidgetType.RECTANGLE:
                return Objects.requireNonNull(actionIndex) + 1;
            case WidgetType.GRAPHIC:
                String targetVerb = getTargetVerb();
                return targetVerb == null||targetVerb.isEmpty() ? Objects.requireNonNull(actionIndex) + 1: 0;
            case WidgetType.TEXT:
            case WidgetType.MODEL:
                return 0;
            default:
                throw new IllegalArgumentException("unsupported widget type");
        }
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (getType()) {
            case WidgetType.LAYER:
            case WidgetType.RECTANGLE:
                return MenuAction.CC_OP.getId();
            case WidgetType.GRAPHIC:
                String targetVerb = getTargetVerb();
                return targetVerb == null||targetVerb.isEmpty() ? MenuAction.CC_OP.getId() : MenuAction.WIDGET_TYPE_2.getId();
            case WidgetType.INVENTORY:
                return MenuAction.WIDGET_TYPE_2.getId();
            case WidgetType.TEXT:
                return MenuAction.WIDGET_TYPE_6.getId();
            case WidgetType.MODEL:
                return MenuAction.WIDGET_TYPE_1.getId();
            default:
                throw new IllegalArgumentException("no menu action for widget type " + getType());
        }
    }

    @Override
    public int getMenuParam0() {
        return getIndex();
    }

    @Override
    public int getMenuParam1() {
        return getId();
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuAction.ITEM_USE_ON_WIDGET.getId();
    }
}
