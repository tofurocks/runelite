package net.runelite.client.plugins.walkerdemo;

import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class WalkerDemoOverlay extends OverlayPanel {
    private final WalkerDemoPlugin plugin;
    public static TraversableWorldPoint[] points;
    private final WalkerDemoConfig config;
    @Inject
    Client client;

    @Inject
    private WalkerDemoOverlay(WalkerDemoPlugin plugin, WalkerDemoConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.plugin = plugin;
        this.config = config;
    }

    public Dimension render(Graphics2D graphics) {
        try {

            Client client = com.runemax.bot.api.game.Client.getInstance();
            if (points != null) {
                for (int i = 0; i < points.length; i++) {
                    TraversableWorldPoint movePoint = points[i];

                    // 11797
                    if (movePoint.getPlane() != client.getPlane()) continue;

                    LocalPoint localPoint = LocalPoint.fromWorld(this.client, movePoint.getX(), movePoint.getY());
                    if (localPoint != null) {

                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        if (poly == null) continue;

                        if (movePoint.isInteractive()) {
                            graphics.setColor(Color.GREEN);
                        } else if (movePoint.isLadder_ascending()) {
                            graphics.setColor(Color.YELLOW);
                        } else if (movePoint.isLadder_descending()) {
                            graphics.setColor(Color.CYAN);
                        } else {
                            if (!LocalPath.reachable(movePoint.getLocalPoint())) {
                                graphics.setColor(Color.RED);
                            } else {
                                graphics.setColor(Color.BLUE);
                            }
                        }

                        graphics.drawPolygon(poly);

                    }
                }
            }
        } catch (Exception e) {
            // sorry not sorry
        }
        return null;
    }
}
