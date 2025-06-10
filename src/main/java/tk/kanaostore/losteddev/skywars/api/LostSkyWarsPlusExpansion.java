package tk.kanaostore.losteddev.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class LostSkyWarsPlusExpansion extends PlaceholderExpansion {

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
        return "lswplus";
    }

    @Override
    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {

        if (params.equals("players_solo")) {
            int playing = CoreLobbies.SOLO_NORMAL + CoreLobbies.SOLO_INSANE;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (WorldServer<?> server : WorldServer.listServers()) {
                    if (server.getMode().equals(SkyWarsMode.SOLO) && (server.getType() == SkyWarsType.NORMAL || server.getType() == SkyWarsType.INSANE)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_doubles")) {
            int playing = CoreLobbies.DOUBLES_NORMAL + CoreLobbies.DOUBLES_INSANE;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (WorldServer<?> server : WorldServer.listServers()) {
                    if (server.getMode().equals(SkyWarsMode.DOUBLES) && (server.getType() == SkyWarsType.NORMAL || server.getType() == SkyWarsType.INSANE)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_ranked")) {
            int playing = CoreLobbies.DOUBLES_RANKED + CoreLobbies.SOLO_RANKED;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (WorldServer<?> server : WorldServer.listServers()) {
                    if (server.getType().equals(SkyWarsType.RANKED)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_duels")) {
            int playing = CoreLobbies.SOLO_DUELS + CoreLobbies.DOUBLES_DUELS;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (WorldServer<?> server : WorldServer.listServers()) {
                    if (server.getType().equals(SkyWarsType.DUELS)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("rank_prefix")) {
            return Rank.getRank(player).getColoredName();
        }

        return null;
    }
}
