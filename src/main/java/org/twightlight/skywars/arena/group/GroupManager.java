package org.twightlight.skywars.arena.group;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

public class GroupManager {

    private static final Map<String, ArenaGroup> GROUPS = new LinkedHashMap<>();
    private static final Logger LOGGER = SkyWars.LOGGER.getModule("GroupManager");

    public static void setup() {
        GROUPS.clear();
        File file = new File("plugins/LostSkyWars/groups.yml");
        if (!file.exists()) {
            SkyWars.getInstance().saveResource("groups.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                new InputStreamReader(SkyWars.getInstance().getResource("groups.yml")));
        config.setDefaults(defaults);

        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection == null) {
            LOGGER.log(Logger.Level.WARNING, "No groups found in groups.yml!");
            return;
        }

        for (String key : groupsSection.getKeys(false)) {
            ConfigurationSection section = groupsSection.getConfigurationSection(key);
            if (section == null) continue;

            ArenaGroup group = new ArenaGroup(key, section);
            GROUPS.put(key, group);
        }


        for (ArenaGroup group : GROUPS.values()) {
            List<String> shared = group.getSharedCosmetics();

            for (String group1 : shared) {
                ArenaGroup arenaGroup = get(group1);
                List<String> shared1 = arenaGroup.getSharedCosmetics();
                if (!shared1.contains(group.getId())) {
                    shared1.add(group.getId());
                }

            }
        }

        LOGGER.log(Logger.Level.INFO, "Loaded " + GROUPS.size() + " arena groups!");
    }

    public static ArenaGroup get(String id) {
        return GROUPS.get(id);
    }

    public static ArenaGroup getOrDefault(String id) {
        ArenaGroup group = GROUPS.get(id);
        if (group == null && !GROUPS.isEmpty()) {
            return GROUPS.values().iterator().next();
        }
        return group;
    }

    public static Collection<ArenaGroup> getGroups() {
        return Collections.unmodifiableCollection(GROUPS.values());
    }

    public static Set<String> getGroupIds() {
        return Collections.unmodifiableSet(GROUPS.keySet());
    }

    public static void reload() {
        setup();
    }

    public static ArenaGroup fromLegacy(String mode, String type) {
        if (type == null) type = "normal";
        if (mode == null) mode = "solo";
        mode = mode.toLowerCase();
        type = type.toLowerCase();

        if (type.equals("duels")) {
            return get("duels");
        } else if (type.equals("ranked")) {
            if (mode.equals("doubles")) {
                return get("ranked_doubles");
            }
            return get("ranked_solo");
        } else if (type.equals("insane")) {
            if (mode.equals("doubles")) {
                return get("doubles_insane");
            }
            return get("solo_insane");
        } else {
            if (mode.equals("doubles")) {
                return get("doubles");
            }
            return get("solo");
        }
    }
}
