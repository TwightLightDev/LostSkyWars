package org.twightlight.skywars.modules.lobbysettings.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.lobbysettings.User;

import java.util.Optional;

public class WorldChangeEvent implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        YamlWrapper wrapper = new YamlWrapper(SkyWars.getInstance(), "locations", SkyWars.getInstance().getDataFolder().getPath());

        String worldName = wrapper.getString("lobby").split("; ")[0];
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase(worldName)) {
            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                Optional.ofNullable(User.getFromUUID(e.getPlayer().getUniqueId())).orElse(new User(e.getPlayer())).enable();
            }, 5L);
            return;
        }
        if (User.getFromUUID(e.getPlayer().getUniqueId()) != null)
            User.getFromUUID(e.getPlayer().getUniqueId()).disable();
    }
}
