package org.twightlight.skywars.cosmetics.perk;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.cosmetics.perk.perks.*;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.config.YamlWrapper;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PerkManager {

    private static final List<Perk> PERKS = new ArrayList<>();
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Perks");
    private static final YamlWrapper CONFIG = YamlWrapper.getConfig("perks");

    private static String decisiveStrikeId;

    public static void register(Perk perk) {
        PERKS.add(perk);
    }

    public static void setupPerks() {
        PERKS.clear();

        checkIfAbsent("arrowrecovery");
        new ArrowRecovery();

        checkIfAbsent("blazingarrow");
        new BlazingArrows();

        checkIfAbsent("bulldozer");
        new Bulldozer();

        checkIfAbsent("endermastery");
        new EnderMastery();

        checkIfAbsent("juggernaut");
        new Juggernaut();

        checkIfAbsent("knowledge");
        new Knowledge();

        checkIfAbsent("nourishment");
        new Nourishment();

        checkIfAbsent("luckycharm");
        new LuckyCharm();

        checkIfAbsent("voidmaster");
        new VoidMaster();

        checkIfAbsent("decisivestrike");
        DecisiveStrike ds = new DecisiveStrike();
        decisiveStrikeId = String.valueOf(ds.getId());
    }

    public static boolean isDecisiveStrike(Player player) {
        if (decisiveStrikeId == null) return false;
        int dsId;
        try {
            dsId = Integer.parseInt(decisiveStrikeId);
        } catch (NumberFormatException ex) {
            return false;
        }
        Perk dsPerk = getById(dsId);
        if (dsPerk == null || !(dsPerk instanceof DecisiveStrike)) return false;
        DecisiveStrike ds = (DecisiveStrike) dsPerk;
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return false;
        if (!ds.isSelected(account)) return false;
        return ds.isAbleToUse(player) && ThreadLocalRandom.current().nextInt(100) < ds.getPercentage();
    }

    public static Perk getById(int id) {
        for (Perk perk : PERKS) {
            if (perk.getId() == id) {
                return perk;
            }
        }
        return null;
    }

    public static List<Perk> listAll() {
        return Collections.unmodifiableList(PERKS);
    }

    public static List<Perk> listForGroup(String cosmeticsGroupId) {
        List<Perk> result = new ArrayList<>();
        for (Perk perk : PERKS) {
            if (perk.isAllowed(cosmeticsGroupId)) {
                result.add(perk);
            }
        }
        return result;
    }

    public static List<String> loadAllowedGroups(String key) {
        if (CONFIG.contains(key + ".allowed-groups")) {
            return CONFIG.getStringList(key + ".allowed-groups");
        }
        // Default: all CosmeticsGroup IDs (normal, insane, ranked)
        List<String> defaultGroups = new ArrayList<>();
        for (CosmeticsGroup cg : CosmeticsGroup.listAll()) {
            defaultGroups.add(cg.getId());
        }
        CONFIG.set(key + ".allowed-groups", defaultGroups);
        return defaultGroups;
    }

    private static void checkIfAbsent(String key) {
        if (CONFIG.contains(key + ".id")) {
            return;
        }
        try {
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(SkyWars.getInstance().getResource("perks.yml"), "UTF-8"));
            ConfigurationSection section = defaults.getConfigurationSection(key);
            if (section != null) {
                for (String dataKey : section.getKeys(true)) {
                    if (!section.isConfigurationSection(dataKey)) {
                        CONFIG.set(key + "." + dataKey, section.get(dataKey));
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
