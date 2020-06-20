package com.runemax.bot.api.worlds;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.widgets.WidgetInfo;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.grandexchange.GrandExchange;
import com.runemax.bot.api.input.Mouse;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.Widgets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Deprecated
public class Worlds {

    /**
     * BPL: Going to rip all of this out and use Runelite's worldService
     */

    public static final String[] UNSUITABLE_ACTIVITY = {
            "trade", "pvp", "deadman", "skill total", "tournament", "private practice", "unrestricted", "bounty", "beta", "high risk", "twisted league", "claim league points"
    };
    public static final Predicate<World> SUITABLE = world -> Arrays.stream(UNSUITABLE_ACTIVITY).noneMatch(unsuitable -> world.getActivity().toLowerCase().contains(unsuitable));
    public static final Predicate<World> P2P = world -> world.getTypes().contains(WorldType.MEMBERS);

    private static final Point OPEN_LOBBY_WORLD_SELECTOR = new Point(20, 475);
    private static final Point CLOSE_LOBBY_WORLD_SELECTOR = new Point(715, 10);

    public static List<World> all(Predicate<? super World> predicate) {
        if (!areWorldsLoaded()) {
            loadWorlds();
        }

        World[] worlds = Client.getWorldList();

        return Arrays
                .stream(worlds)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static boolean isSwitcherOpen(){
        return Widgets.get(WidgetInfo.WORLD_SWITCHER_LIST).isPresent();
    }

    public static boolean areWorldsLoaded() {
        return Client.getWorldList() != null;
    }

    public static void loadWorlds() {
        GameState gameState = Client.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            Mouse.click(OPEN_LOBBY_WORLD_SELECTOR);
            Sleep.sleep(200);
            clickCloseLobbyWorldSelector();
        } else if (gameState == GameState.LOGGED_IN) {
            openWorldSwitcher();
        } else {
            throw new BotException("game state doesnt make sense: " + gameState);
        }

        Sleep.until(Worlds::areWorldsLoaded, 250, 5000);
    }

    public static World get(int id) {
        return all(world -> world.getId() == id).stream().findFirst().orElseThrow(() -> new BotException("no world for id: " + id));
    }

    public static void changeLobbyWorld(int id) {
        Client.changeWorld(get(id));
    }

    public static void clickCloseLobbyWorldSelector() {
        Mouse.click(CLOSE_LOBBY_WORLD_SELECTOR);
    }

    public static World getBest(Predicate<? super World> predicate, Comparator<? super World> comparator) {
        return all(predicate).stream().min(comparator).orElseThrow(() -> new BotException("no world matched"));
    }

    public static World getRandom(Predicate<? super World> predicate) {
        List<World> all = all(predicate);
        Collections.shuffle(all);
        return all.stream().findFirst().orElseThrow(() -> new BotException("no world matched"));
    }

    public static int getCurrentWorldId() {
        return Client.getWorld();
    }

    public static World getCurrentWorld() {
        return get(Client.getWorld());
    }

    public static void openWorldSwitcher() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.continueSpace();
            }else {
                Movement.walk(Players.getLocal().getWorldLocation());
            }
            return;
        }

        if (GrandExchange.getView() != GrandExchange.View.CLOSED) {
            Movement.walk(Players.getLocal().getWorldLocation());
            return;
        }

        if (Bank.isOpen()) {
            Bank.close();
            return;
        }

        Client.openWorldHopper();
        Sleep.until(Worlds::isSwitcherOpen,5000);
    }

    public static void switchTo(World world) {
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return;
        }

        if (GrandExchange.getView() != GrandExchange.View.CLOSED) {
            Movement.walk(Players.getLocal().getWorldLocation());
            return;
        }

        if (Bank.isOpen()) {
            Bank.close();
            return;
        }

        log.info("switching to: " + world.getId());
        if (Dialog.getOptionsText().stream().anyMatch(s -> s.contains("Yes. In future, only warn about dangerous worlds"))) {
            Dialog.chooseOption("Yes. In future, only warn about dangerous worlds");
            return;
        }

        if (Widgets.get(WidgetInfo.WORLD_SWITCHER_LIST).isEmpty()) {
            openWorldSwitcher();
        }

        Client.hopToWorld(world);
    }
}
