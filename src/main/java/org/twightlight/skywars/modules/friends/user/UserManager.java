package org.twightlight.skywars.modules.friends.user;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.Friend;
import org.twightlight.skywars.modules.friends.friend.FriendList;

import java.util.*;

public class UserManager {
    private Map<UUID, User> users;

    private Friends module;

    public UserManager(Friends plugin) {
        this.module = plugin;
        init();
    }

    private void init() {
        this.users = new HashMap<>();
        if (!this.module.getPlugin().getServer().getOnlinePlayers().isEmpty())
            for (Player user : this.module.getPlugin().getServer().getOnlinePlayers())
                cacheUser(user);
    }

    public void saveUser(OfflinePlayer user) {
        FileConfiguration storage = this.module.getStorageUtil().getFile(user.getUniqueId().toString());
        List<String> friends = new ArrayList<>();
        for (Friend friend : getUser((Player)user).getFriendList().getAllFriends())
            friends.add(friend.getUniqueId().toString());
        storage.set("preferences.allow-friend-requests", Boolean.valueOf(getUser((Player)user).isAllowingRequests()));
        storage.set("friends", friends);
        this.module.getStorageUtil().saveFile(storage, user.getUniqueId().toString());
    }

    public void cacheUser(Player user) {
        if (!this.module.getStorageUtil().fileExists(user.getUniqueId().toString())) {
            this.module.getStorageUtil().createFile(user.getUniqueId().toString());
            FileConfiguration fileConfiguration = this.module.getStorageUtil().getFile(user.getUniqueId().toString());
            fileConfiguration.set("preferences.allow-friend-requests", Boolean.valueOf(true));
            fileConfiguration.set("friends-list-full", Boolean.valueOf(false));
            fileConfiguration.set("friends", new ArrayList());
            this.module.getStorageUtil().saveFile(fileConfiguration, user.getUniqueId().toString());
        }
        FileConfiguration config = this.module.getStorageUtil().getFile(user.getUniqueId().toString());
        List<Friend> friendsList = new ArrayList<>();
        int friendLimit = this.module.getConfig().getInt("options.max-friends.default");
        if (friendsList.size() > friendLimit)
            config.set("friends-list-full", Boolean.valueOf(true));
        for (String uuid : config.getStringList("friends"))
            friendsList.add(new Friend(UUID.fromString(uuid)));
        FriendList friendList = new FriendList(friendsList);
        boolean allowingRequests = config.getBoolean("preferences.allow-friend-requests");
        for (String perm : this.module.getConfig().getConfigurationSection("options.max-friends").getKeys(false)) {
            if (user.hasPermission("friends.limit." + perm))
                friendLimit = this.module.getConfig().getInt("options.max-friends." + perm);
        }
        this.users.put(user.getUniqueId(), new User(friendList, allowingRequests, friendLimit));
    }

    public void uncacheUser(Player user) {
        saveUser((OfflinePlayer)user);
        this.users.remove(user.getUniqueId());
    }

    public User getUser(Player user) {
        return this.users.get(user.getUniqueId());
    }
}
