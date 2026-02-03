package org.twightlight.skywars.hook.decenthologram;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.twightlight.skywars.hook.decenthologram.holograms.Leaderboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private UUID uuid;

    private static Map<UUID, User> userMap = new HashMap<>();

    public Leaderboard.Mode viewingMode;

    public User(Player p) {
        this.uuid = p.getUniqueId();
        userMap.put(uuid, this);
        viewingMode = Leaderboard.Mode.OVERALL;

        DecentHologramsHook.loadStatus.thenApply((b) -> {
            loadLeaderboards();
            return b;
        });
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static User getFromPlayer(Player p) {
        return userMap.getOrDefault(p.getUniqueId(), null);
    }

    public static User getFromUUID(UUID uuid) {
        return userMap.getOrDefault(uuid, null);
    }

    public UUID getUUID() {
        return uuid;
    }

    public static User removeUser(User user) {
        return userMap.remove(user.uuid);
    }

    public Leaderboard.Mode getViewingMode() {
        return viewingMode;
    }


    public void switchLeaderboards() {
        int nextModeIndex = (viewingMode.ordinal() + 1) % Leaderboard.Mode.values().length;
        Leaderboard.Mode newMode = Leaderboard.Mode.values()[nextModeIndex];
        switchLeaderboards(newMode);
    }

    public void switchLeaderboards(Leaderboard.Mode mode) {
        if (mode == null) return;

        DecentHologramsHook.getLeaderboards(viewingMode).forEach(leaderboard -> leaderboard.hide(this));
        DecentHologramsHook.getLeaderboards(mode).forEach(leaderboard -> leaderboard.show(this));
        this.viewingMode = mode;
    }

    public static Collection<User> getUsers() {
        return userMap.values();
    }

    public void loadLeaderboards() {
        viewingMode = Leaderboard.Mode.OVERALL;

        DecentHologramsHook.getLeaderboards().forEach(leaderboard -> leaderboard.hide(this));

        DecentHologramsHook.getLeaderboards(viewingMode).forEach(leaderboard -> leaderboard.show(this));
    }
}
