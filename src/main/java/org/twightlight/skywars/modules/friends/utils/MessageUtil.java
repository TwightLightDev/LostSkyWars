package org.twightlight.skywars.modules.friends.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageUtil {
    public void message(CommandSender user, String message) {
        user.sendMessage(color(message));
    }

    public void message(Player user, TextComponent message) {
        user.spigot().sendMessage((BaseComponent)message);
    }

    public void message(CommandSender user, List<String> message) {
        for (String line : message)
            message(user, line);
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
