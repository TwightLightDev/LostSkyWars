package org.twightlight.skywars.modules.lobbysettings.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.lobbysettings.User;

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
        return "lobbysettings";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        User user = User.getFromUUID(player.getUniqueId());
        if (user == null) return "";
        if (params.equals("fly")) {
            return String.valueOf(user.isFlyEnable());
        } else if (params.equals("speed")) {
            return String.valueOf(user.getSpeed());
        } else if (params.equals("jumpboost")) {
            return String.valueOf(user.getJumpBoost());
        } else if (params.equals("vanish")) {
            return String.valueOf(user.isVanish());
        } else if (params.equals("showscoreboard")) {
            return String.valueOf(user.isScoreboardVisible());
        } else if (params.equals("showparticles")) {
            return String.valueOf(user.isParticlesVisible());
        } else if (params.equals("showchat")) {
            return String.valueOf(user.isChatVisible());
        } else if (params.equals("showplayers")) {
            return String.valueOf(user.isPlayerVisible());
        } else if (params.equals("showblood")) {
            return String.valueOf(user.isBloodVisible());
        } else {
            return "";
        }
    }
}
