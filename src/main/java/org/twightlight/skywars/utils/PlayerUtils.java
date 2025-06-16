package org.twightlight.skywars.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.world.type.DuelsServer;

public class PlayerUtils {

    public static String replaceAll(Player player, Player player2, String string) {
        String lastColor = StringUtils.getLastColor(player2.getDisplayName());
        if (lastColor.isEmpty()) {
            lastColor = "§7";
        }

        Account account = Database.getInstance().getAccount(player2.getUniqueId());
        if (account != null && account.getServer() != null && account.getServer() instanceof DuelsServer) {
            DuelsServer duels = (DuelsServer) account.getServer();
            if (duels.getState() == SkyWarsState.WAITING || duels.getState() == SkyWarsState.STARTING) {
                lastColor += "§k";
            }
        }

        if (Main.placeholderapi) {
            return replaceAll(player,
            PlaceholderAPI.setPlaceholders(player2, string.replace("{player2}", player2.getName())
                            .replace("{display2}", StringUtils.formatColors("%vault_prefix%%player_name%%vault_suffix%"))
                            .replace("{colored2}", lastColor + player2.getName() + (lastColor.contains("§k") ? "§r" : ""))));
        }

        return replaceAll(player,
                string.replace("{player2}", player2.getName())
                        .replace("{display2}", player2.getDisplayName())
                        .replace("{colored2}", lastColor + player2.getName() + (lastColor.contains("§k") ? "§r" : "")));
    }

    public static String replaceAll(Player player, String string) {
        String lastColor = StringUtils.getLastColor(player.getDisplayName());
        if (lastColor.isEmpty()) {
            lastColor = "§7";
        }

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account != null && account.getServer() != null && account.getServer() instanceof DuelsServer) {
            DuelsServer duels = (DuelsServer) account.getServer();
            if (duels.getState() == SkyWarsState.WAITING || duels.getState() == SkyWarsState.STARTING) {
                lastColor += "§k";
            }
        }

        if (Main.placeholderapi) {
            return PlaceholderAPI.setPlaceholders(player,  string.replace("{player}", player.getName())
                    .replace("{display}", StringUtils.formatColors("%vault_prefix%%player_name%%vault_suffix%"))
                    .replace("{colored}", lastColor + player.getName() + (lastColor.contains("§k") ? "§r" : "")));
        }

        return string.replace("{player}", player.getName())
                .replace("{display}", player.getDisplayName())
                .replace("{colored}", lastColor + player.getName() + (lastColor.contains("§k") ? "§r" : ""));
    }
}
