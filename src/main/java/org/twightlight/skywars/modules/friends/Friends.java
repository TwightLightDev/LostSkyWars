package org.twightlight.skywars.modules.friends;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.Modules;
import org.twightlight.skywars.modules.friends.commands.FriendCmd;
import org.twightlight.skywars.modules.friends.config.MainConfig;
import org.twightlight.skywars.modules.friends.friend.FriendRequestManager;
import org.twightlight.skywars.modules.friends.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.friends.user.UserManager;
import org.twightlight.skywars.modules.friends.utils.MessageUtil;
import org.twightlight.skywars.modules.friends.utils.StorageUtil;

import java.io.File;

public class Friends extends Modules {
    private FriendRequestManager friendRequestManager;

    private UserManager hfUserManager;

    private MessageUtil messageUtil;

    private StorageUtil storageUtil;

    private MainConfig mainConfig;

    private static Friends instance;

    public Friends() {
        loadUtils();
        loadConfig();
        loadCommands();
        loadListeners();
        instance = this;
    }

    private void loadUtils() {
        this.friendRequestManager = new FriendRequestManager(this);
        this.storageUtil = new StorageUtil(this);
        this.hfUserManager = new UserManager(this);
        this.messageUtil = new MessageUtil();
    }

    private void loadConfig() {
        File modules = new File(getPlugin().getDataFolder().getPath() + "/modules/friends");
        if (!modules.exists()) {
            modules.mkdirs();
        }
        mainConfig = new MainConfig(getPlugin(), "config", getPlugin().getDataFolder().getPath() + "/modules/friends");
    }

    private void loadCommands() {
        getPlugin().getCommand("friend").setExecutor((CommandExecutor)new FriendCmd(this));
    }

    private void loadListeners() {
        PluginManager pluginManager = SkyWars.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinEvent(this), (Plugin)this.getPlugin());
    }

    public FriendRequestManager getFriendRequestManager() {
        return this.friendRequestManager;
    }

    public UserManager getUserManager() {
        return this.hfUserManager;
    }

    public boolean isInteger(String potentialInt) {
        try {
            Integer.parseInt(potentialInt);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public MessageUtil getMessageUtil() {
        return this.messageUtil;
    }

    public StorageUtil getStorageUtil() {
        return this.storageUtil;
    }

    public YamlConfiguration getConfig() {
        return mainConfig.getYml();
    }

    public static Friends getInstance() {
        return instance;
    }
}
