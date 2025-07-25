package org.twightlight.skywars.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.protocollib.ProtocolListener;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;

public class ProtocolLibHook {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("ProtocolLibHook");

    private static final ProtocolListener LISTENER = new ProtocolListener();

    public static void setupProtocolLib() {
        getProtocolManager().addPacketListener(LISTENER);
    }

    public static void destroyProtocolLib() {
        getProtocolManager().removePacketListener(LISTENER);

        LOGGER.log(Level.INFO, "ProtocolLib found, destroying StatsNPC...");
    }

    public static ProtocolManager getProtocolManager() {
        return ProtocolLibrary.getProtocolManager();
    }
}
