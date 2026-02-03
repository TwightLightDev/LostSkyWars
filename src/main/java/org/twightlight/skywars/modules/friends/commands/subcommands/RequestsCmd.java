package org.twightlight.skywars.modules.friends.commands.subcommands;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.FriendRequest;

public class RequestsCmd extends SubCommand {
    public RequestsCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        for (String line : getModule().getConfig().getStringList("messages.requests"))
            getModule().getMessageUtil().message(user, line.replace("%0%", String.valueOf(1)).replace("%1%", String.valueOf(1)));
        for (FriendRequest request : getModule().getFriendRequestManager().getFriendRequests(((Player)user).getUniqueId())) {
            TextComponent username = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eFrom " + (request.getSender().isOnline() ? Bukkit.getPlayer(request.getSender().getUniqueId()).getDisplayName() : request.getSender().getName())));
            TextComponent optionAccept = new TextComponent(ChatColor.translateAlternateColorCodes('&', " " + getModule().getConfig().getString("messages.request-option-accept")));
            ClickEvent clickEventAccept = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + request.getSender().getName());
            HoverEvent hoverEventAccept = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bClick to accept the friend request"))).create());
            optionAccept.setClickEvent(clickEventAccept);
            optionAccept.setHoverEvent(hoverEventAccept);
            TextComponent optionDeny = new TextComponent(ChatColor.translateAlternateColorCodes('&', " " + getModule().getConfig().getString("messages.request-option-deny")));
            ClickEvent clickEventDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + request.getSender().getName());
            HoverEvent hoverEventDeny = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bClick to deny the friend request"))).create());
            optionDeny.setClickEvent(clickEventDeny);
            optionDeny.setHoverEvent(hoverEventDeny);
            getModule().getMessageUtil().message((Player)user, new TextComponent(new BaseComponent[] { (BaseComponent)username, (BaseComponent)optionAccept, (BaseComponent)optionDeny }));
        }
    }
}
