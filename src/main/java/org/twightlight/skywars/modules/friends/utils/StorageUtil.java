package org.twightlight.skywars.modules.friends.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.twightlight.skywars.modules.friends.Friends;

import java.io.File;
import java.io.IOException;

public class StorageUtil {
    private Friends module;

    public StorageUtil(Friends plugin) {
        this.module = plugin;
    }

    public void createFile(String name) {
        File file = new File(this.module.getPlugin().getDataFolder(), "modules/friends/data/" + name + ".yml");
        if (file.exists())
            return;
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadFile(FileConfiguration config, String name) {
        try {
            config.load(new File(this.module.getPlugin().getDataFolder(), "modules/friends/data/" + name + ".yml"));
        } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(FileConfiguration config, String name) {
        try {
            config.save(new File(this.module.getPlugin().getDataFolder(), "modules/friends/data/" + name + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(String name) {
        return (new File(this.module.getPlugin().getDataFolder(), "modules/friends/data/" + name + ".yml")).exists();
    }

    public FileConfiguration getFile(String name) {
        return (FileConfiguration) YamlConfiguration.loadConfiguration(new File(this.module.getPlugin().getDataFolder(), "modules/friends/data/" + name + ".yml"));
    }
}
