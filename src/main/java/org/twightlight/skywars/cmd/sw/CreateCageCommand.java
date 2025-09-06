package org.twightlight.skywars.cmd.sw;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsCage;
import org.twightlight.skywars.setup.cage.CageSetupSession;
import org.twightlight.skywars.utils.StringCheckerUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.*;

public class CreateCageCommand extends SubCommand{

    public CreateCageCommand() {
        super("createcage");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {


        if (args.length < 1) {
            player.sendMessage("§cUse /lsw createcage <id>");
            return;
        }

        new CageSetupSession(player, args[0]);
        player.sendMessage("§aCage setup started for: §e" + args[0]);

    }

    @Override
    public String getUsage() {
        return "createcage <id>";
    }

    @Override
    public String getDescription() {
        return "Create a new SkyWars Cage.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }

}
