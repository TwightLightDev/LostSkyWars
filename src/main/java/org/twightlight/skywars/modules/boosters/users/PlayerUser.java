package org.twightlight.skywars.modules.boosters.users;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.boosters.streams.Activating;
import org.twightlight.skywars.modules.boosters.boosters.streams.Queue;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerUser extends org.twightlight.skywars.modules.boosters.users.User implements ModulesUser {
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

    public void removeBooster(String sBooster) {
        Booster booster = BoosterManager.getBoosters().get(sBooster);
        if (booster == null) return;

        Booster.BoosterType type = booster.getType();

        List<String> storage = (type == Booster.BoosterType.PERSONAL)
                ? personal_storage
                : network_storage;

        storage.remove(sBooster);
        save();
    }

    @Override
    public boolean addToQueue(UUID uuid, String booster) {
        boolean isQueue;
        Booster booster1 = BoosterManager.getBoosters().get(booster);
        isQueue = activating.isFull();
        boolean b = super.addToQueue(uuid, booster);
        if (b) {
            if (isQueue) {
                sendMessage(Boosters.getLanguage().getList("messages.boosters.queue").stream().map((line) -> {
                    return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                }).collect(Collectors.toList()));
            } else {
                sendMessage(Boosters.getLanguage().getList("messages.boosters.active").stream().map((line) -> {
                    return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                }).collect(Collectors.toList()));
            }
        }
        return b;
    }

    public void save() {
        Boosters.getDatabase().updateData(Bukkit.getPlayer(uuid), personal_storage, Booster.BoosterType.PERSONAL.getStorageColumn());
        Boosters.getDatabase().updateData(Bukkit.getPlayer(uuid), network_storage, Booster.BoosterType.NETWORK.getStorageColumn());
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
        int max = 1;
        for (String key : configuration) {
            if (Bukkit.getPlayer(uuid).hasPermission(Boosters.getConfig().getString("general."+ str +".cap." + key))) {
                max = Math.max(max, Integer.parseInt(key));
            }
        }
        return max;
    }

    public int getMaxCap(String str) {
        Set<String> configuration = Boosters.getConfig().getYml().getConfigurationSection("general."+ str +".cap").getKeys(false);
        if (configuration == null) return 1;
        int max = 1;
        for (String key : configuration) {
            max = Math.max(max, Integer.parseInt(key));
        }
        return max;
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
