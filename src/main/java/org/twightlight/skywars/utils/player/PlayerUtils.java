package org.twightlight.skywars.utils.player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.string.StringUtils;

public class PlayerUtils {

    public static String replaceAll(Player player, Player player2, String string) {

        if (SkyWars.placeholderapi) {
            return replaceAll(player,
                    PlaceholderAPI.setPlaceholders(player2, string.replace("{player2}", player2.getName())
                            .replace("{display2}", StringUtils.formatColors("%vault_prefix%%player_name%%vault_suffix%"))
                    ));
        }

        return replaceAll(player,
                string.replace("{player2}", player2.getName())
                        .replace("{display2}", player2.getDisplayName()));
    }

    public static String replaceAll(Player player, String string) {

        if (SkyWars.placeholderapi) {
            return PlaceholderAPI.setPlaceholders(player, string.replace("{player}", player.getName())
                    .replace("{display}", StringUtils.formatColors("%vault_prefix%%player_name%%vault_suffix%")));
        }

        return string.replace("{player}", player.getName())
                .replace("{display}", player.getDisplayName());
    }

    public static void sendSuggestText(Player player, String displayText, String suggestedText) {
        TextComponent message = new TextComponent(displayText);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedText));
        player.spigot().sendMessage(message);
    }
}
