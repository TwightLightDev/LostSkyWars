package org.twightlight.skywars.hook.guilds.listeners;

import me.leoo.guilds.bukkit.Guilds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.hook.guilds.donation.Donator;
import org.twightlight.skywars.hook.guilds.menus.DonationMenu;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack i = e.getCurrentItem();
        String name = ChatColor.translateAlternateColorCodes('&', Guilds.get().getMenuConfig().getString("guilds.menu.home.items.guild-donation.name"));
        List<String> lore = Guilds.get().getMenuConfig().getYml().getStringList("guilds.menu.home.items.guild-donation.lore").stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
        if (i != null && i.hasItemMeta()) {
            ItemMeta meta = i.getItemMeta();
            if (meta.hasDisplayName() && meta.hasLore() && name.equals(meta.getDisplayName()) && lore.equals(meta.getLore())) {
                DonationMenu.open(Donator.getFromUUID(e.getWhoClicked().getUniqueId()));
            }
        }
    }
}
