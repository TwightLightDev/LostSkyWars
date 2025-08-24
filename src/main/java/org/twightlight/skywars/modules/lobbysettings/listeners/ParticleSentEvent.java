package org.twightlight.skywars.modules.lobbysettings.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.lobbysettings.User;

public class ParticleSentEvent {
    private static ProtocolManager manager;

    public static void init() {
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(SkyWars.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Server.WORLD_PARTICLES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                User user = User.getFromUUID(player.getUniqueId());
                if (user == null) return;
                if (!user.isParticlesVisible()) {
                    event.setCancelled(true);
                }
            }
        });
    }
}
