package org.twightlight.skywars.modules.boosters;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.boosters.boosters.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.Queue;

import java.util.*;

public class User {
    private UUID uuid;

    private static Map<UUID, User> userList = new HashMap<>();

    private Queue queue;
    private Activating activating;
    private List<Booster> personal_storage;
    private List<Booster> server_storage;

    public User(Player p) {
        uuid = p.getUniqueId();
        Boosters.getDatabase().createPlayerData(p);
        userList.put(uuid, this);
        queue = new Queue(this, getCap("queue"), Booster.BoosterType.PERSONAL);
        activating = new Activating(this, getCap("active"), Booster.BoosterType.PERSONAL, queue);
        personal_storage = Boosters.getDatabase().getData(p, Booster.BoosterType.PERSONAL.getStorageColumn(), new TypeToken<List<Booster>>() {
        }, new ArrayList<>());
        server_storage = Boosters.getDatabase().getData(p, Booster.BoosterType.SERVER.getStorageColumn(), new TypeToken<List<Booster>>() {
        }, new ArrayList<>());
    }

    public static User getFromUUID(UUID uuid) {
        return userList.get(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void activateBooster(Booster booster) {
        activating.add(booster);
    }

    public void deactivateBooster(int booster) {
        activating.remove(booster);
    }

    public void addToQueue(Booster booster) {
        if (activating.isEmpty()) {
            activateBooster(booster);
        } else {
            queue.add(booster);
        }
    }

    public void removeFromQueue(int booster) {
        queue.remove(booster);
    }

    public void addBooster(Booster booster, Booster.BoosterType type) {
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.add(booster);
        } else {
            server_storage.add(booster);
        }
    }

    public void removeBooster(Booster booster, Booster.BoosterType type) {
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.remove(booster);
        } else {
            server_storage.remove(booster);
        }
    }

    public void removeBooster(int booster, Booster.BoosterType type) {
        if (type == Booster.BoosterType.PERSONAL) {
            personal_storage.remove(booster);
        } else {
            server_storage.remove(booster);
        }
    }

    public float getTotalMultiplier(Booster.Currency currency) {
        return (float) (1F + activating.getActivatingBooster().stream()
                                .filter(b -> b.getCurrency() == currency)
                                .mapToDouble(b -> b.getAmplifier() - 1)
                                .sum());
    }

    public boolean hasBooster() {
        return !activating.isEmpty();
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

}
