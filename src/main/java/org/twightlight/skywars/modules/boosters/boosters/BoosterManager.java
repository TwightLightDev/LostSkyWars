package org.twightlight.skywars.modules.boosters.boosters;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.utils.TimeUtils;

import java.util.*;

public class BoosterManager {
    public static void give(String name, Booster booster) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null && player.isOnline()) {
            PlayerUser user = PlayerUser.getFromUUID(player.getUniqueId());
            user.addBooster(booster.getId());
            user.save();
        } else {
            Boosters.getDatabase().getOfflineData(name, booster.getType().getStorageColumn(), new TypeToken<List<String>>() {
            }, new ArrayList<>()).thenAccept((o) -> {
                o.add(booster.getId());
                Boosters.getDatabase().appendOfflineData(name, booster, booster.getType().getStorageColumn());
            });
        }
    }

    private static final Map<String, Booster> registry = new HashMap<>();

    public static void init() {
        Set<String> boosterPaths = Boosters.getBoostersConfig().getYml().getConfigurationSection("boosters").getKeys(false);
        for (String b : boosterPaths) {
            registry.put(b, Booster.parseFromYaml(Boosters.getBoostersConfig(), "boosters." + b, b));
        }
        Boosters.getInstance().getLogger().log(Logger.Level.INFO, "Loaded " + registry.size() + " boosters!");

    }

    public static Map<String, Booster> getBoosters() {
        return registry;
    }

    public static String getDurationString(Booster booster) {
        int duration = booster.getDuration();
        return TimeUtils.convertTime(duration * 1000L, " Day", " Hour", " Minute", " Second");
    }

    public static String getAmplifierString(Booster booster) {
        float amplifier = booster.getAmplifier();
        return String.valueOf(amplifier).replace(".0", "");
    }

    public static String getCurrencyString(Booster booster) {
        Booster.Currency currency = booster.getCurrency();
        return currency.getName();
    }

    public static String getColor(Booster booster) {
        Booster.Currency currency = booster.getCurrency();
        return currency.getColorCode();
    }

    public static List<String> replaceLore(List<String> lore, List<String> status) {
        int index = lore.indexOf("{status}");
        if (index == -1) {
            return lore;
        }

        lore.subList(index, index+1).clear();
        lore.addAll(index, status);
        return lore;
    }
}
