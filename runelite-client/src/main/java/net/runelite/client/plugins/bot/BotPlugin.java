package net.runelite.client.plugins.bot;

import com.runemax.bot.api.BotManager;
import com.runemax.bot.api.account.Account;
import com.runemax.bot.api.account.Credentials;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.webwalker.WebWalker;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.quest.QuestPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Bot",
        hidden = true
)
@Slf4j
public class BotPlugin extends Plugin {

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private EventBus eventBus;

    @com.google.inject.Inject
    private WorldService worldService;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DrawManager drawManager;

    private NavigationButton navButton;
    private BotOverlay overlay;
    private BotManager botManager;

    @Subscribe
    private void onGameTick(GameTick gameTick) {
        WebWalker.tick(Client.getInstance());
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged stateChanged) {

        if (stateChanged.getGameState() != GameState.LOGIN_SCREEN) {
            return;
        }

        log.info("Starting botplugin thread");

        new Thread(() -> {

            // Sleep just to be sure the UI is loaded
            Sleep.sleep(2000);

            // Set Username / Password
            String usernameArgument = System.getProperty("username");
            String passwordArgument = System.getProperty("password");
            String worldArgument = System.getProperty("world");
            String scriptArgument = System.getProperty("script");

            log.info("username: {}", usernameArgument);
            log.info("password: {}", passwordArgument);
            log.info("world: {}", worldArgument);
            log.info("script: {}", scriptArgument);

            // Set Credentials
            if (usernameArgument != null && passwordArgument != null) {
                Credentials credentials = new Credentials(usernameArgument, passwordArgument);
                Account.setCredentials(credentials);
                Sleep.sleep(1000);
            }

            // Set World
            if (worldArgument != null) {
                int worldNumber = Integer.parseInt(worldArgument);
                int correctedWorldNumber = worldNumber < 300 ? worldNumber + 300 : worldNumber;
                if (Client.getWorld() != correctedWorldNumber) {
                    final WorldResult worldResult = worldService.getWorlds();
                    if (worldResult != null) {
                        final World world = worldResult.findWorld(correctedWorldNumber);
                        if (world != null) {
                            final net.runelite.api.World rsWorld = Client.createWorld();
                            rsWorld.setActivity(world.getActivity());
                            rsWorld.setAddress(world.getAddress());
                            rsWorld.setId(world.getId());
                            rsWorld.setPlayerCount(world.getPlayers());
                            rsWorld.setLocation(world.getLocation());
                            rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
                            Client.changeWorld(rsWorld);
                            log.debug("Applied new world {}", correctedWorldNumber);
                        } else {
                            log.warn("World {} not found.", correctedWorldNumber);
                        }
                    } else {
                        log.warn("Failed to lookup worlds.");
                        System.exit(1);
                    }
                }
            }

            // Set Script
            if (scriptArgument != null) {
                startScript(botManager, scriptArgument);
            }

        }).start();
    }

    @Override
    protected void startUp() throws Exception {
        botManager = new BotManager();
        navButton = NavigationButton.builder()
            .tooltip("Bot")
            .icon(ImageUtil.getResourceStreamFromClass(QuestPlugin.class, "info_icon.png"))
            .priority(-420)
            .panel(new BotPanel(botManager))
            .build();

        eventBus.subscribe(MenuEntryAdded.class, this, Interact.driver::onMenuEntryAdded);
        clientToolbar.addNavigation(navButton);
        overlayManager.add(overlay = new BotOverlay());
    }

    private void startScript(BotManager botManager, String scriptName) {
        for (Class<? extends BotScript> scriptClass : BotManager.loadScripts()) {
            if (scriptClass.getAnnotation(ScriptMeta.class).value().equalsIgnoreCase(scriptName)) {
                botManager.startScript(scriptClass);
                return;
            }
        }

        log.warn("cant find script: " + scriptName);
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
        overlayManager.remove(overlay);
    }
}
