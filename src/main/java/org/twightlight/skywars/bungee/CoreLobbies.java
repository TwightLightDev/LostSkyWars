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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoreLobbies {

    private static final Map<String, Integer> groupPlayerCounts = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Integer>> mapSelectorData = new ConcurrentHashMap<>();

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

    public static int getPlayerCount(String groupId) {
        return groupPlayerCounts.getOrDefault(groupId, 0);
    }

    public static void setPlayerCount(String groupId, int count) {
        groupPlayerCounts.put(groupId, count);
    }

    public static Map<String, Integer> getMapSelector(String groupId) {
        return mapSelectorData.getOrDefault(groupId, Collections.emptyMap());
    }

    public static void setMapSelector(String groupId, Map<String, Integer> data) {
        mapSelectorData.put(groupId, data);
    }

    public static void setupLobbies() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(SkyWars.getInstance(), "LostSWAPI");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArenaGroup group : GroupManager.getGroups()) {
                    String groupId = group.getId();
                    writeCount(groupId);
                    writeMapSelector(groupId);
                }
            }
        }.runTaskTimer(SkyWars.getInstance(), 20L, 40L);
    }

    private static Player getAnyOnlinePlayer() {
        for (Player p : Bukkit.getOnlinePlayers()) return p;
        return null;
    }
}
