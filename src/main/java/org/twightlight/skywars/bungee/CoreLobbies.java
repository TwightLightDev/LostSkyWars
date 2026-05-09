package org.twightlight.skywars.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoreLobbies {

    // Group-ID based player counts
    private static final Map<String, Integer> groupPlayerCounts = new ConcurrentHashMap<>();

    public static void writeLobby(Player player) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account != null) account.save();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Lobby");
        player.sendPluginMessage(SkyWars.getInstance(), "LostSWAPI", out.toByteArray());
    }

    public static void writeCount(String groupId) {
        Player player = getAnyOnlinePlayer();
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Count");
        out.writeUTF(groupId);
        player.sendPluginMessage(SkyWars.getInstance(), "LostSWAPI", out.toByteArray());
    }

    public static void writeMinigame(Player player, String groupId, String map) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account != null) account.save();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Play");
        out.writeUTF(groupId);
        out.writeUTF(map);
        player.sendPluginMessage(SkyWars.getInstance(), "LostSWAPI", out.toByteArray());
    }

    public static void writeMapSelector(String groupId) {
        Player player = getAnyOnlinePlayer();
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MapSelector");
        out.writeUTF(groupId);
        player.sendPluginMessage(SkyWars.getInstance(), "LostSWAPI", out.toByteArray());
    }

    /**
     * Gets player count for any group ID.
     */
    public static int getPlayerCount(String groupId) {
        return groupPlayerCounts.getOrDefault(groupId, 0);
    }

    /**
     * Sets up lobbies. Iterates over all registered groups instead of mode/type enums.
     */
    public static void setupLobbies() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(SkyWars.getInstance(), "LostSWAPI");

        new BukkitRunnable() {
            @Override
            public void run() {
                // Iterate all registered groups from GroupManager
                for (ArenaGroup group : GroupManager.all()) {
                    String groupId = group.getId();
                    writeCount(groupId);

                    // Write map selector for non-duels groups
                    if (!group.hasTrait("duels")) {
                        writeMapSelector(groupId);
                    }
                }
            }
        }.runTaskTimer(SkyWars.getInstance(), 20L, 40L);
    }

    private static Player getAnyOnlinePlayer() {
        for (Player p : Bukkit.getOnlinePlayers()) return p;
        return null;
    }
}
