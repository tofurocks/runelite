package net.runelite.client.plugins.walkerdemo;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
    name = "Walker Demo"
)
public class WalkerDemoPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WalkerDemoConfig config;
    @Inject
    private Client client;
    @Inject
    private WalkerDemoOverlay overlay;

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
//        if (event.getGroup().equals("destination")) {
//            log.info("destination changed");
//            Player player = this.client.getLocalPlayer();
//            if (player != null) {
//                WalkerDemoOverlay.points = WebWalker.getPath(player, config.x(), config.y(), true);
//            }
//        }
    }

    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    @Provides
    WalkerDemoConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WalkerDemoConfig.class);
    }

    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
    }
}
