package org.twightlight.skywars.modules.friends.commands.subcommands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.Friend;
import org.twightlight.skywars.modules.friends.friend.FriendRequest;
import org.twightlight.skywars.modules.friends.user.User;

public class AddCmd extends SubCommand {
    public AddCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        String potentialFriendsName;
        if (!validatePermissions(user))
            return;
        User hfUser = getModule().getUserManager().getUser((Player)user);
        if (hfUser.hasReachedFriendLimit()) {
            for (String line : getModule().getConfig().getStringList("messages.max-friends"))
                getModule().getMessageUtil().message(user, line.replace("%0%", String.valueOf(hfUser.getFriendLimit())));
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("add")) {
                getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-arguments").replace("%0%", "friend add <name>"));
                return;
            }
            potentialFriendsName = args[0];
        } else {
            potentialFriendsName = args[1];
        }
        if (potentialFriendsName.length() > 16 || potentialFriendsName.replaceAll("[a-zA-Z0-9_]", "").length() > 0) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-input").replace("%0%", potentialFriendsName));
            return;
        }
        if (potentialFriendsName.equalsIgnoreCase(user.getName())) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.cannot-friend-yourself"));
            return;
        }
        OfflinePlayer potentialFriend;
        if ((potentialFriend = getModule().getPlugin().getServer().getOfflinePlayer(potentialFriendsName)) == null || !potentialFriend.hasPlayedBefore()) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.player-not-found").replace("%0%", potentialFriendsName));
            return;
        }
        for (Friend friend : getModule().getUserManager().getUser((Player)user).getFriendList().getAllFriends()) {
            if (friend.getUniqueId().equals(potentialFriend.getUniqueId())) {
                getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.already-friends"));
                return;
            }
        }
        if (getModule().getStorageUtil().getFile(potentialFriend.getUniqueId().toString()).getBoolean("friends-list-full")) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.request-not-allowed"));
            return;
        }
        if (!getModule().getStorageUtil().getFile(potentialFriend.getUniqueId().toString()).getBoolean("preferences.allow-friend-requests")) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.request-not-allowed"));
            return;
        }
        getModule().getFriendRequestManager().addRequest(new FriendRequest(((Player)user).getUniqueId(), potentialFriend.getUniqueId(), getModule().getConfig().getInt("options.friend-add-timeout")));
        if (potentialFriend.isOnline()) {
            for (String line : getModule().getConfig().getStringList("messages.friend-request-header"))
                getModule().getMessageUtil().message((CommandSender)Bukkit.getPlayer(potentialFriend.getUniqueId()), line.replace("%0%", ((Player)user).getDisplayName()));
            TextComponent optionsMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', getModule().getConfig().getString("messages.friend-request-options")));
            TextComponent optionAccept = new TextComponent(ChatColor.translateAlternateColorCodes('&', getModule().getConfig().getString("messages.request-option-accept")));
            ClickEvent clickEventAccept = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + user.getName());
            HoverEvent hoverEventAccept = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bClick to accept the friend request"))).create());
            optionAccept.setClickEvent(clickEventAccept);
            optionAccept.setHoverEvent(hoverEventAccept);
            TextComponent optionDeny = new TextComponent(ChatColor.translateAlternateColorCodes('&', getModule().getConfig().getString("messages.request-option-deny")));
            ClickEvent clickEventDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + user.getName());
            HoverEvent hoverEventDeny = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bClick to deny the friend request"))).create());
            optionDeny.setClickEvent(clickEventDeny);
            optionDeny.setHoverEvent(hoverEventDeny);
            TextComponent spacer = new TextComponent(ChatColor.translateAlternateColorCodes('&', getModule().getConfig().getString("messages.spacer")));
            getModule().getMessageUtil().message(Bukkit.getPlayer(potentialFriend.getUniqueId()), new TextComponent(new BaseComponent[] { (BaseComponent)optionsMessage, (BaseComponent)optionAccept, (BaseComponent)spacer, (BaseComponent)optionDeny }));
            getModule().getMessageUtil().message((CommandSender)Bukkit.getPlayer(potentialFriend.getUniqueId()), getModule().getConfig().getStringList("messages.friend-request-footer"));
        }
        for (String line : getModule().getConfig().getStringList("messages.request-sent"))
            getModule().getMessageUtil().message(user, line.replace("%0%", potentialFriend.getName()).replace("%1%", String.valueOf(getModule().getConfig().getInt("options.friend-add-timeout"))));
    }
}