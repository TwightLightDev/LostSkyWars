package org.twightlight.skywars.integration.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;

public class PacketEventsIntegration {
    private static PacketEventsAPI<?> api;
    public static Logger LOGGER = SkyWars.LOGGER.getModule("PacketEventsHook");

    public static void setupPacketEvents() {
        LOGGER.log(Logger.Level.INFO, "PacketEvents found, hooking...");


        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(SkyWars.getInstance()));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();

        api = PacketEvents.getAPI();
    }

    public static void disable() {
        api.terminate();
    }

    public static PacketEventsAPI<?> getPacketEventsAPI() {
        return api;
    }
}
