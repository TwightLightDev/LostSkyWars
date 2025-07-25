package org.twightlight.skywars.database;

import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;

public class BukkitLoader {

    public static void start() {
        org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyWars.getInstance(), () -> {
            Database.getInstance().listAccounts().stream().filter(account -> account.inLobby() && account.getScoreboard() != null).forEach(account -> account.getScoreboard().update());
        }, 0, 20);

        org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyWars.getInstance(), () -> {
            Database.getInstance().listAccounts().stream().filter(account -> account.getScoreboard() != null).forEach(account -> account.getScoreboard().scroll());
        }, 0, Language.scoreboards$animation$update);
    }
}
