package org.twightlight.skywars.modules.privategames;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.privategames.settings.*;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.SkyWarsEvent;
import org.twightlight.skywars.world.WorldServer;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PrivateGamesUser {

    UUID uuid;
    GameSpeedSetting gameSpeedSetting;
    GameTimeSetting gameTimeSetting;
    HealthMultiplySetting healthMultiplySetting;
    InstantKillSetting instantKillSetting;

    public PrivateGamesUser(Player p) {
        this.uuid = p.getUniqueId();
        PrivateGames.getStorage().addUser(p, this);
        PrivateGames.getStorage().getDatabase().createPlayerData(p);
        for (SettingTypes type : SettingTypes.values()) {
            PrivateGames.getStorage().getDatabase().createPlayerData(p, type);
        }
        PrivateGames.getStorage().getDatabase().updateData(p, 0, "isEnable");
        gameSpeedSetting = new GameSpeedSetting(GameSpeedSetting.getBaseValue(p), p);
        gameTimeSetting = new GameTimeSetting(GameTimeSetting.getBaseValue(p), p);
        healthMultiplySetting = new HealthMultiplySetting(HealthMultiplySetting.getBaseValue(p), p);
        instantKillSetting = new InstantKillSetting(InstantKillSetting.getBaseValue(p), p);

    }

    public void togglePrivateGame() {
        Player p = getPlayer();
        Integer booleanInt = PrivateGames.getStorage().getDatabase().getData(p, "isEnable", new TypeToken<Integer>() {}, 0);
        if (booleanInt == 0) {
            if (SkyWars.lostparties) {
                io.github.losteddev.parties.api.Party party = io.github.losteddev.parties.api.Party.getPartyByMember(p);
                if (party != null && party.getSize() >= 2) {
                    if (!party.getOwnerName().equalsIgnoreCase(p.getName())) {
                        PrivateGames.getLanguage().getList("privategames.party.not_leader").
                                forEach(line ->
                                        p.sendMessage(ChatColor.
                                                translateAlternateColorCodes('&', line)));
                        return;
                    }
                    PrivateGames.getStorage().getDatabase().updateData(p, 1, "isEnable");
                    PrivateGames.getLanguage().getList("privategames.toggle.enable").
                            forEach(line ->
                                    p.sendMessage(ChatColor.
                                            translateAlternateColorCodes('&', line).replace("{player}", p.getDisplayName())));
                } else {
                    PrivateGames.getLanguage().getList("privategames.party.not_found").
                            forEach(line ->
                                    p.sendMessage(ChatColor.
                                            translateAlternateColorCodes('&', line)));
                }
            } else {
                PrivateGames.getStorage().getDatabase().updateData(p, 1, "isEnable");
                PrivateGames.getLanguage().getList("privategames.toggle.enable").
                        forEach(line ->
                                p.sendMessage(ChatColor.
                                        translateAlternateColorCodes('&', line).replace("{player}", p.getDisplayName())));
            }
        } else if (booleanInt == 1) {
            PrivateGames.getStorage().getDatabase().updateData(p, 0, "isEnable");
            PrivateGames.getLanguage().getList("privategames.toggle.disable").
                    forEach(line ->
                            p.sendMessage(ChatColor.
                                    translateAlternateColorCodes('&', line).replace("{player}", p.getDisplayName())));
        }
    }

    public boolean isEnablePrivateGame() {
        Integer booleanInt = PrivateGames.getStorage().getDatabase().getData(getPlayer(), "isEnable", new TypeToken<Integer>() {}, 0);
        if (booleanInt == 1) {
            return true;
        } else if (booleanInt == 0) {
            return false;
        }
        return false;
    }

    public void connect(Account account, WorldServer<?> server) {
        WorldServer<?> privateServer = server.cloneServer(true, server.getConfig().getId() + "_" + account.getPlayer().getUniqueId().toString());
        privateServer.setServerOwner(this);
        Map<Integer, SkyWarsEvent> newTimeLine = new TreeMap<>(Comparator.reverseOrder());
        Double multiplier = getGameSpeedSetting().getValue();
        Map<Integer, SkyWarsEvent> oldTimeLine = server.getTimeline();
        oldTimeLine.forEach((i, e) -> {
            int newPoint = (int) Math.round(i / multiplier);
            newTimeLine.put(newPoint, e);
        });
        privateServer.setTimeline(newTimeLine);
        privateServer.connect(account);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public GameSpeedSetting getGameSpeedSetting() {
        return gameSpeedSetting;
    }

    public GameTimeSetting getGameTimeSetting() {
        return gameTimeSetting;
    }

    public HealthMultiplySetting getHealthMultiplySetting() {
        return healthMultiplySetting;
    }

    public InstantKillSetting getInstantKillSetting() {
        return instantKillSetting;
    }
}
