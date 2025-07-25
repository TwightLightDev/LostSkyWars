package org.twightlight.skywars.modules.friends.commands.subcommands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.Friend;
import org.twightlight.skywars.modules.friends.user.User;

public class ListCmd extends SubCommand {
    public ListCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        User hfUser = getModule().getUserManager().getUser((Player)user);
        int page = 1;
        if (args.length > 1)
            if (getModule().isInteger(args[1]) && Integer.valueOf(args[1]).intValue() <= hfUser.getFriendList().getFriendsListSize() / 8) {
                page = Integer.valueOf(args[1]).intValue();
            } else {
                getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.invalid-page-number"));
                return;
            }
        TextComponent last = new TextComponent("                «");
                last.setColor(ChatColor.YELLOW);
        TextComponent next = new TextComponent("»");
                next.setColor(ChatColor.YELLOW);
        HoverEvent nextHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&cClick to view page " + String.valueOf(Integer.valueOf(page + 1))))).create());
        ClickEvent nextClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + String.valueOf(Integer.valueOf(page + 1)));
        HoverEvent lastHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&cClick to view page " + String.valueOf(Integer.valueOf(page - 1))))).create());
        ClickEvent lastClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + String.valueOf(Integer.valueOf(page - 1)));
        next.setHoverEvent(nextHoverEvent);
        next.setClickEvent(nextClickEvent);
        last.setHoverEvent(lastHoverEvent);
        last.setClickEvent(lastClickEvent);
        TextComponent headerPage = new TextComponent(ChatColor.translateAlternateColorCodes('&', getModule().getConfig().getString("messages.list-page").replace("%1%", String.valueOf(page)).replace("%2%", String.valueOf((hfUser.getFriendList().getFriendsListSize() / 8 < 1) ? 1 : (hfUser.getFriendList().getFriendsListSize() / 8)))));
        for (String line : getModule().getConfig().getStringList("messages.list-header"))
            getModule().getMessageUtil().message(user, line);
        getModule().getMessageUtil().message((Player)user, new TextComponent(new BaseComponent[] { (page > 1) ? (BaseComponent)last : (BaseComponent)new TextComponent("                 "), (BaseComponent)headerPage, (BaseComponent)next }));
        if (hfUser.getFriendList().getFriendsListSize() <= 0) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.no-friends"));
        } else {
            for (Friend friend : hfUser.getFriendList().getFriends(page))
                getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.list-format").replace("%0%", friend.getName()).replace("%1%", friend.isOnline() ? getModule().getConfig().getString("messages.list-status.online").replace("%0%", friend.getStatus()) : getModule().getConfig().getString("messages.list-status.offline")));
        }
        getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.list-footer"));
    }
}
