package com.runemax.bot.api.varps;

import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.OnGameThread;
import net.runelite.api.Varbits;

public class Varps {
    public static int getBit(int id){
        return OnGameThread.invokeAndWait(()-> Client.getVarbitValue(Client.getVarps(), id));
    }

    public static int getBit(Varbits varbits){
        return getBit(varbits.getId());
    }

    public static int get(int id){
        return Client.getVarpValue(Client.getVarps(), id);
    }
}
