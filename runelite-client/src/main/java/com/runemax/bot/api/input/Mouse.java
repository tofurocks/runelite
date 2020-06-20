package com.runemax.bot.api.input;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import net.runelite.api.Point;

import java.awt.*;
import java.awt.event.*;

public class Mouse {
    public synchronized static void click(int x, int y, boolean left) {
        Canvas canvas = Client.getCanvas();

        MouseListener[] mouseListeners = canvas.getMouseListeners();
        long currTime = System.currentTimeMillis();
        MouseEvent pressedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, currTime, 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mousePressed(pressedEvent);
        }

        MouseEvent clickedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, currTime, 0, x, y, 1, false);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mouseClicked(clickedEvent);
        }

        Sleep.sleep(15, 50);
        MouseEvent releasedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mouseReleased(releasedEvent);
        }
    }

    public synchronized static void click(int x, int y) {
        click(x, y, true);
    }

    public synchronized static void click(Point point, boolean left) {
        click(point.getX(), point.getY(), left);
    }

    public synchronized static void click(Point point) {
        click(point.getX(), point.getY());
    }

    public synchronized static void click() {
        Point mouseCanvasPosition = Client.getMouseCanvasPosition();
        click(mouseCanvasPosition.getX(), mouseCanvasPosition.getY());
    }

    public synchronized static void clicked(int x, int y){
        Canvas canvas = Client.getCanvas();
        MouseListener[] mouseListeners = canvas.getMouseListeners();
        MouseEvent clickedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mouseClicked(clickedEvent);
        }
    }

    public synchronized static void pressed(int x, int y, int button){
        Canvas canvas = Client.getCanvas();
        MouseListener[] mouseListeners = canvas.getMouseListeners();
        MouseEvent pressedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, button == 2, button);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mousePressed(pressedEvent);
        }
    }

    public synchronized static void moved(int x, int y) {
        Canvas canvas = Client.getCanvas();
        MouseMotionListener[] mouseMotionListeners = canvas.getMouseMotionListeners();
        MouseEvent movedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false);
        for (MouseMotionListener mouseMotionListener : mouseMotionListeners) {
            mouseMotionListener.mouseMoved(movedEvent);
        }
    }

    public synchronized static void dragged(int x, int y) {
        Canvas canvas = Client.getCanvas();
        MouseMotionListener[] mouseMotionListeners = canvas.getMouseMotionListeners();
        MouseEvent movedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, x, y, 0, false);
        for (MouseMotionListener mouseMotionListener : mouseMotionListeners) {
            mouseMotionListener.mouseMoved(movedEvent);
        }
    }

    public synchronized static void released(int x, int y){
        Canvas canvas = Client.getCanvas();
        MouseMotionListener[] mouseMotionListeners = canvas.getMouseMotionListeners();
        MouseEvent movedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 0, false);
        for (MouseMotionListener mouseMotionListener : mouseMotionListeners) {
            mouseMotionListener.mouseMoved(movedEvent);
        }
    }

    public synchronized static void exited(int x, int y) {
        Canvas canvas = Client.getCanvas();
        MouseListener[] mouseListeners = canvas.getMouseListeners();
        MouseEvent exitedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, x, y, 0, false);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mouseExited(exitedEvent);
        }
    }

    public synchronized static void entered(int x, int y) {
        Canvas canvas = Client.getCanvas();
        MouseListener[] mouseListeners = canvas.getMouseListeners();
        MouseEvent pressedEvent = new MouseEvent(canvas, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, x, y, 0, false);
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.mouseEntered(pressedEvent);
        }
    }

    public synchronized static void wheel(int wheelRotation) {
        Canvas canvas = Client.getCanvas();
        MouseWheelListener[] mouseWheelListeners = canvas.getMouseWheelListeners();
        Point mouseCanvasPosition = Client.getMouseCanvasPosition();
        MouseWheelEvent scrollEvent = new MouseWheelEvent(canvas, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mouseCanvasPosition.getX(), mouseCanvasPosition.getY(), 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, wheelRotation);
        for (MouseWheelListener mouseWheelListener : mouseWheelListeners) {
            mouseWheelListener.mouseWheelMoved(scrollEvent);
        }
    }
}
