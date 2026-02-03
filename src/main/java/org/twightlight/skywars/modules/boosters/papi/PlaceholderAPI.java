package org.twightlight.skywars.modules.boosters.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.modules.boosters.users.User;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PlaceholderAPI extends PlaceholderExpansion {

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
        return "boosters";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        User user = null;
        if (params.startsWith("network")) {
            user = ServerUser.getServerUser();
        } else if (params.startsWith("personal")) {
            user = PlayerUser.getFromUUID(player.getUniqueId());
        }

        String[] elements = params.split("_");
        elements = Arrays.copyOfRange(elements, 1, elements.length);

        if (user == null) return "";
        return "";
    }
}
