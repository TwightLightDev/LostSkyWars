package org.twightlight.skywars.integration.guilds.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.leoo.guilds.bukkit.manager.GuildsManager;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;

public class GuildsShopExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "TwightLight";
    }

    @Override
    public String getIdentifier() {
        return "guildsshop";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equals("canopen")) {
            return GuildsManager.getByPlayer(player) == null ? "false" : "true";
        }

        return null;
    }
}
