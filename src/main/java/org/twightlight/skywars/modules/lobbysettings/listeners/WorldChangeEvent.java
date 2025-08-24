package org.twightlight.skywars.modules.lobbysettings.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;
import org.twightlight.skywars.modules.lobbysettings.User;

public class WorldChangeEvent implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        YamlWrapper wrapper = new YamlWrapper(SkyWars.getInstance(), "locations", SkyWars.getInstance().getDataFolder().getPath());

        String worldName = wrapper.getString("lobby").split("; ")[0];
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase(worldName)) {
            User.getFromUUID(e.getPlayer().getUniqueId()).enable();
            return;
        }
        User.getFromUUID(e.getPlayer().getUniqueId()).disable();
    }
}
