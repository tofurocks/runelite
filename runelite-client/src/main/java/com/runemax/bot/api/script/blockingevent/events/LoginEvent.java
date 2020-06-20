package com.runemax.bot.api.script.blockingevent.events;

import com.runemax.bot.api.account.Account;
import com.runemax.bot.api.account.Credentials;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.input.Keyboard;
import com.runemax.bot.api.input.Mouse;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.blockingevent.BlockingEvent;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.scripts.Store;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import org.jboss.aerogear.security.otp.Totp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class LoginEvent extends BlockingEvent {
    public static boolean needHop = true;
    private static final Point CANCEL_POINT = new Point(394, 303);
    private static final Point EXISTING_USER_POINT = new Point(396, 276);
    private static final Point TRY_AGAIN_POINT = new Point(312, 258);

    public interface State {
        int MAIN_MENU = 0;
        int BETA_WORLD = 1;
        int ENTER_CREDENTIALS = 2;
        int INVALID_CREDENTIALS = 3;
        int AUTHENTICATOR = 4;
        int DISABLED = 12;
        int BEEN_DISCONNECTED = 24;
    }

    private Map<String, Supplier<Integer>> loginMessageHandlers = new HashMap<>();

    public void addLoginMessageHandler(String message, Supplier<Integer> handler) {
        loginMessageHandlers.put(message.toLowerCase(), handler);
    }

    private Map<Integer, Supplier<Integer>> loginStateHandlers = new HashMap<>();

    public void addLoginStateHandler(Integer state, Supplier<Integer> handler) {
        loginStateHandlers.put(state, handler);
    }

    @Override
    public boolean validate() {
        return Client.getGameState() != GameState.LOGGED_IN;
    }

    @Override
    public int execute() {
        GameState gameState = Client.getGameState();

        if (gameState == GameState.STARTING) return 500;

        //if we are logging in, wait for welcome screen
        if (gameState == GameState.LOGGING_IN) {
            Sleep.until(() -> Client.getGameState() != GameState.LOGGING_IN, 100, 60 * 1000);
            if (Client.getGameState() == GameState.LOGGED_IN) {
                Sleep.until(WelcomeEvent::check, 2000);
                Sleep.until(() -> Skills.getLevel(Skill.HITPOINTS) >= 10, 500);
                Sleep.until(() -> !Inventory.isEmpty(), 200);
            }
            return 10;
        }

        if (gameState != GameState.LOGIN_SCREEN && gameState != GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            return 50;
        }

        String loginMessage1 = Client.getLoginResponse1().toLowerCase();
        for (String message : loginMessageHandlers.keySet()) {
            if (loginMessage1.contains(message.toLowerCase())) {
                Integer returnn = loginMessageHandlers.get(message).get();
                if (returnn != null) {
                    return returnn;
                } else {
                    break;
                }
            }
        }

        if (loginMessage1.contains("onnection timed out") || loginMessage1.contains("error connecting to server")) {
            Sleep.sleep(20 * 1000);
        } else if (loginMessage1.contains("need a members")) {
            log.warn("World requested requires membership. Got error '{}'", loginMessage1);
            System.exit(255);
        } else if (loginMessage1.contains("update")) {
            log.error("World requested is being updated. Got error '{}'", loginMessage1);
            System.exit(255);
        } else if (loginMessage1.contains("total") && loginMessage1.contains("level")) {
            log.error("World requested requires a total level. Got error '{}'", loginMessage1);
            System.exit(255);
        }

        Credentials credentials = Account.getCredentials();
        if (credentials == null) {
            log.info("no account");
            return -1;
        }

        int loginState = Client.getLoginIndex();
        Supplier<Integer> loginStateHandler = loginStateHandlers.get(loginState);
        if (loginStateHandler != null) {
            Integer returnn = loginStateHandler.get();
            if (returnn != null) {
                return returnn;
            }
        }

        switch (loginState) {
            case State.ENTER_CREDENTIALS:
                Client.setUsername(credentials.getUser());
                Client.setPassword(credentials.getPassword());
                Keyboard.enter();
                Keyboard.enter();
                break;
            case State.MAIN_MENU:
                Mouse.click(EXISTING_USER_POINT);
                break;
            case State.AUTHENTICATOR:
                String auth = credentials.getAuth();
                if (auth == null) {
                    log.info("no auth");
                    return -1;
                }
                Client.setOtp(new Totp(auth).now());
                Keyboard.enter();
                break;
            case State.BEEN_DISCONNECTED:
                log.info("disconected state");
                Mouse.click(CANCEL_POINT);
                break;
            case State.BETA_WORLD:
                log.error("Shouldn't be using BETA worlds");
                System.exit(255);
                break;
            case State.DISABLED:
                log.error("Account disabled! Deleting from guy server");
                com.sun.jersey.api.client.Client client = Store.getJerseyClient();
                WebResource webResource = client
                        .resource("http://localhost:8080/guys/" + Account.getCredentials().getUser());
                webResource.type("application/json").delete();
                return -1;
            case State.INVALID_CREDENTIALS:
                return -1;
            default:
                log.info("cant handle state: " + loginState);
                break;
        }
        return 500;
    }
}
