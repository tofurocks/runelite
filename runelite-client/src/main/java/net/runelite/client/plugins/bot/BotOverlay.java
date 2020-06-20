package net.runelite.client.plugins.bot;

import lombok.Setter;
import net.runelite.client.ui.overlay.*;

import java.awt.*;

public class BotOverlay extends Overlay {
    @Setter
    private static RenderableEntity renderable = null;

    BotOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if(renderable != null){
            return renderable.render(g);
        }
        return null;
    }
}
