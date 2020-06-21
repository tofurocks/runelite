package net.runelite.client.plugins.walkerdemo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("destination")
public interface WalkerDemoConfig extends Config {
    @ConfigItem(
        keyName = "x",
        name = "Destination X",
        description = "",
        position = 1
    )
    default int x() {
        return 0;
    }

    @ConfigItem(
        keyName = "y",
        name = "Destination Y",
        description = "",
        position = 2
    )
    default int y() {
        return 0;
    }
}
