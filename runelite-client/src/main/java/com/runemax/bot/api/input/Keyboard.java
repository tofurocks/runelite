package com.runemax.bot.api.input;

import java.awt.Canvas;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import lombok.Setter;

import javax.annotation.Nonnull;

public class Keyboard {
    @Setter
    private static Supplier<Integer> keystrokeDelaySupplier = ()-> Rand.nextInt(100, 300);

    public synchronized static void type(char c) {
        Canvas canvas = Client.getCanvas();
        KeyListener[] keyListeners = canvas.getKeyListeners();

        long time = System.currentTimeMillis();

        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
        KeyEvent pressedEvent = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, time, 0, keyCode, c, KeyEvent.KEY_LOCATION_STANDARD);
        for (KeyListener keyListener : keyListeners) {
            keyListener.keyPressed(pressedEvent);
        }

        KeyEvent typedEvent = new KeyEvent(canvas, KeyEvent.KEY_TYPED, time, 0, 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);
        for (KeyListener keyListener : keyListeners) {
            keyListener.keyTyped(typedEvent);
        }

        Sleep.sleep(keystrokeDelaySupplier.get());
        KeyEvent releasedEvent = new KeyEvent(canvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keyCode, c, KeyEvent.KEY_LOCATION_STANDARD);
        for (KeyListener keyListener : keyListeners) {
            keyListener.keyReleased(releasedEvent);
        }
    }

    public synchronized static void enter() {
        type((char) KeyEvent.VK_ENTER);
    }

    public synchronized static void type(@Nonnull String text, boolean sendEnter){
        char[] chars = text.toCharArray();
        for (char c : chars) {
            type(c);
            Sleep.sleep(Rand.nextInt(0, 200));
        }
        if (sendEnter) {
            enter();
        }
    }

    public synchronized static void type(@Nonnull String text) {
        type(text, false);
    }

    public synchronized static void focusGained(){
        Canvas canvas = Client.getCanvas();
        FocusEvent focusGainedEvent = new FocusEvent(canvas, FocusEvent.FOCUS_GAINED, false);
        for (FocusListener focusListener : canvas.getFocusListeners()) {
            focusListener.focusGained(focusGainedEvent);
        }
    }

    public synchronized static void focusLost(){
        Canvas canvas = Client.getCanvas();
        FocusEvent focusLostEvent = new FocusEvent(canvas, FocusEvent.FOCUS_LOST, true);
        for (FocusListener focusListener : canvas.getFocusListeners()) {
            focusListener.focusGained(focusLostEvent);
        }
    }

    public synchronized static void pressed(int keyCode){
        Canvas canvas = Client.getCanvas();
        KeyEvent pressedEvent = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        for (KeyListener keyListener : canvas.getKeyListeners()) {
            keyListener.keyPressed(pressedEvent);
        }
    }
}
