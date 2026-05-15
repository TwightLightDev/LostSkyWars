package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.player.ranked.League;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.utils.PlayerUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AsyncPlayerChatListener extends Listeners {

    private static final Map<String, Long> flood = new HashMap<>();

    private static final DecimalFormat df = new DecimalFormat("###.#");

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        flood.remove(evt.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerChat(AsyncPlayerChatEvent evt) {
        if (evt.isCancelled() || !Language.lobby$chat$enabled) {
            return;
        }

        evt.setCancelled(true);

        Player player = evt.getPlayer();
        if (!player.hasPermission("lostskywars.chat.delay")) {
            long start = flood.containsKey(player.getName()) ? flood.get(player.getName()) : 0;
            if (start > System.currentTimeMillis()) {
                double time = (start - System.currentTimeMillis()) / 1000.0;
                if (time > 0.1) {
                    String timeString = df.format(time).replace(",", ".");
                    if (timeString.endsWith("0")) {
                        timeString = timeString.substring(0, timeString.lastIndexOf("."));
                    }

                    player.sendMessage(Language.lobby$chat$delay_message.replace("{time}", timeString));
                    return;
                }
            }

            flood.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Language.lobby$chat$delay_time));
        }

        String color = Rank.getRank(player).getPermission().equals("none") ? "\u00a77" : "\u00a7f";
        if (player.hasPermission("lostskywars.chat.color")) {
            evt.setMessage(StringUtils.formatColors(evt.getMessage()));
        }

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        Arena server = account.getArena();
        ArenaGroup group = server != null ? server.getGroup() : null;

        if (server != null && group != null && group.hasTrait("no_chat_waiting")
                && (server.getState().canJoin() || server.getState() == org.twightlight.skywars.api.server.SkyWarsState.STARTING)) {
            return;
        }

        String level = Level.getByLevel(account.getLevel()).getLevel(account);
        for (Account accounts : Database.getInstance().listAccounts()) {
            Player players = accounts.getPlayer();
            if (server == null && accounts.getArena() == null) {
                players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
            } else if (accounts.getArena() != null && accounts.getArena().equals(server)) {
                if (server.isSpectator(player) && !server.isSpectator(players)) {
                    continue;
                }

                if (server.isSpectator(player)) {
                    players.sendMessage(
                            PlayerUtils.replaceAll(player, Language.lobby$chat$format_spectator.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
                } else {
                    if (group != null && group.hasTrait("has_elo")) {
                        League league = account.getLeague();
                        String leagueName = league != null ? league.getName() : "";
                        players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format_ranked.replace("{level}", level).replace("{league}", leagueName)
                                .replace("{points}", account.getEloFormatted()).replace("{color}", color).replace("{message}", evt.getMessage())));
                    } else if (group != null && group.hasTrait("no_chat_waiting")) {
                        players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format_duels.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
                    } else {
                        players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatSession session = ChatSession.getSession(player);

        if (session != null) {
            event.setCancelled(true);
            session.handleInput(event.getMessage());
        }
    }
}
