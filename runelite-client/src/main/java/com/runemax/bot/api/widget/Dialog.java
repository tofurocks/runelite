package com.runemax.bot.api.widget;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.input.Keyboard;

import javax.annotation.Nonnull;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Dialog {
    public static void enterAmount(int amount) {
        Sleep.until(Dialog::isEnterAmountOpen, 2000);
        Keyboard.type(String.valueOf(amount), true);
    }

    public static boolean isEnterAmountOpen() {
        Widget enterAmount = Widgets.get(WidgetInfo.CHATBOX_FULL_INPUT);
        return enterAmount.isPresent() && !enterAmount.isHidden();
    }

    public static boolean isOpen() {
        Widget scrollBar = Widgets.get(162, 559);
        return !scrollBar.isEmpty() && scrollBar.isHidden();
    }

    public static void continueSpace() {
        Keyboard.type((char) KeyEvent.VK_SPACE);
    }

    public static boolean isLegacyContinue() {
        Widget legacyContinue = Widgets.get(WidgetInfo.CHATBOX_FULL_INPUT);
        return !legacyContinue.isEmpty() && !legacyContinue.isHidden();
    }

    public static void continueLegacy() {
        Client.runScript(299, 1, 1);
    }

    public static boolean canContinue() {
        return isNpcContinue() || isPlayerContinue() || isWeirdContinue() || isWeirderContinue() || isWeirdererContinue() || isLevelUpContinue() || isWeirderererContinue();
    }

    private static boolean isPlayerContinue() {
        return Client.getWidget(WidgetID.DIALOG_PLAYER_GROUP_ID, 3) != null;
    }

    private static boolean isNpcContinue() {
        Widget npcContinue = Widgets.get(WidgetInfo.DIALOG_NPC_CONTINUE);
        return !npcContinue.isEmpty();
    }

    //Idk when this one comes up but it does
    private static boolean isWeirdContinue() {
        return !Widgets.get(193, 3).isEmpty();
    }

    //somewhere on tutotiral in start house
    private static boolean isWeirderContinue() {
        return !Widgets.get(229, 2).isEmpty();
    }

    private static boolean isWeirdererContinue() {//https://i.imgur.com/jVMIDYD.png
        Widget widget = Widgets.get(WidgetInfo.DIALOG_SPRITE);
        return !widget.isEmpty() && !widget.getChild(2).isEmpty();
    }

    public static boolean isWeirderererContinue(){//https://i.imgur.com/9XdODWa.png
        Widget widget = Widgets.get(11, 4);
        return widget.isPresent() && !widget.isHidden();
    }

    private static boolean isLevelUpContinue() {
        return !Widgets.get(WidgetInfo.LEVEL_UP).isEmpty();
    }

    public static boolean isViewingOptions() {
        return !getOptions().isEmpty();
    }

    @Nonnull
    public static List<Widget> getOptions() {
        Widget widget = Widgets.get(WidgetID.DIALOG_OPTION_GROUP_ID, 1);

        if (widget.isEmpty()) return Collections.emptyList();

        List<Widget> widgets = Arrays.asList(widget.getChildren());

        if (widgets.size() <= 1) return Collections.emptyList();

        return new ArrayList<>(widgets.subList(1, widgets.size()));
    }

    @Nonnull
    public static List<String> getOptionsText() {
        return getOptions()
                .stream()
                .map(Widget::getText)
                .collect(Collectors.toList());
    }

    public static void chooseOption(int index) {
        List<Widget> options = getOptions();
        if(options.size() - 1 < index) throw new IllegalArgumentException("tryna idnex:" + index + " size: " + options.size());
        options.get(index).interact(0);
    }

    public static void chooseOption(String... partialTexts) {
        for (Widget option : getOptions()) {
            for (String partialText : partialTexts) {
                if(option.getText().toLowerCase().contains(partialText.toLowerCase())){
                    option.interact(0);
                    return;
                }
            }
        }

        throw new BotException("no option matched");
    }

    public static boolean hasOption(String... containsIgnoreCase){
        for (String s : Dialog.getOptionsText()) {
            String lowerCase = s.toLowerCase();
            for (String s1 : containsIgnoreCase) {
                if(lowerCase.contains(s1.toLowerCase())) return true;
            }
        }

        return false;
    }

    public static void processDialog(String... partialText){
        if(Dialog.canContinue()){
            Dialog.continueSpace();
            return;
        }

        if(Dialog.isViewingOptions()){
            Dialog.chooseOption(partialText);
            return;
        }

        if(!Dialog.isOpen()){
            throw new BotException("dialog not open");
        }
    }
}
