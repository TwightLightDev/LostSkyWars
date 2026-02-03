package org.twightlight.skywars.hook.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;

public class ProtocolLibHook {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("ProtocolLibHook");

    private static final ProtocolListener LISTENER = new ProtocolListener();

    public static void setupProtocolLib() {
        getProtocolManager().addPacketListener(LISTENER);
    }

    public static void destroyProtocolLib() {
        getProtocolManager().removePacketListeners(SkyWars.getInstance());

        LOGGER.log(Level.INFO, "ProtocolLib found, destroying StatsNPC...");
    }

    public static ProtocolManager getProtocolManager() {
        return ProtocolLibrary.getProtocolManager();
    }
}
