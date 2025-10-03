package org.twightlight.skywars.modules.boosters.users;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.boosters.streams.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.streams.Queue;

import java.util.*;

public class PlayerUser extends org.twightlight.skywars.modules.boosters.users.User {
    private UUID uuid;

    private static Map<UUID, PlayerUser> userList = new HashMap<>();

    private List<String> personal_storage;
    private List<String> network_storage;

    public PlayerUser(Player p) {
        uuid = p.getUniqueId();
        Boosters.getDatabase().createPlayerData(p);
        userList.put(uuid, this);
        queue = new Queue(this, getCap("queue"), Booster.BoosterType.PERSONAL);
        activating = new Activating(this, getCap("active"), Booster.BoosterType.PERSONAL, queue);
        personal_storage = Boosters.getDatabase().getData(p, Booster.BoosterType.PERSONAL.getStorageColumn(), new TypeToken<List<String>>() {
        }, new ArrayList<>());
        network_storage = Boosters.getDatabase().getData(p, Booster.BoosterType.NETWORK.getStorageColumn(), new TypeToken<List<String>>() {
        }, new ArrayList<>());
    }

    public static PlayerUser getFromUUID(UUID uuid) {
        return userList.get(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void addBooster(String Sbooster) {
        Booster booster = BoosterManager.getBoosters().get(Sbooster);
        Booster.BoosterType type = booster.getType();
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.add(Sbooster);
        } else {
            network_storage.add(Sbooster);
        }
    }

    public void removeBooster(String Sbooster) {
        Booster booster = BoosterManager.getBoosters().get(Sbooster);
        Booster.BoosterType type = booster.getType();
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.remove(Sbooster);
        } else {
            network_storage.remove(Sbooster);
        }
    }

    public void removeBooster(int booster, Booster.BoosterType type) {
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.remove(booster);
        } else {
            network_storage.remove(booster);
        }
    }

    public int getCap(String str) {
        Set<String> configuration = Boosters.getConfig().getYml().getConfigurationSection("general."+ str +".cap").getKeys(false);
        if (configuration == null) return 1;
        for (String key : configuration) {
            if (Bukkit.getPlayer(uuid).hasPermission(Boosters.getConfig().getString("general."+ str +".cap." + key))) {
                return Integer.parseInt(key);
            }
            else return 1;
        }
        return 1;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public List<String> getPersonalStorage() {
        return personal_storage;
    }

    public List<String> getNetworkStorage() {
        return network_storage;
    }

    public void sendMessage(String msg) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

    }

    public void sendMessage(List<String> msgs) {
        Player p = Bukkit.getPlayer(uuid);
        msgs.forEach(line -> {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        });
    }

    public static PlayerUser removeUser(PlayerUser user) {
        return userList.remove(user.uuid);
    }

    public static Map<UUID, PlayerUser> getUsers() {
        return userList;
    }
}
