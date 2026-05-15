package org.twightlight.skywars.setup.chests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.setup.InventoryHolder;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.ItemBuilder;

import java.util.Arrays;

public class Browser extends Menu {
    private ConfigWrapper config;

    private Browser(ConfigWrapper config) {
        super(45, true);
        this.config = config;
        holder.setCloseExecutable((e) -> {
        });

        setItems(holder);
    }

    private void setItems(InventoryHolder holder) {
        holder.getButtonsMap().clear();
        holder.getInventory().clear();
        int slot = 0;
        for (String chesttype : config.getSection("").getKeys(false)) {
            setItem(slot, new ItemBuilder(XMaterial.CHEST).setName(ChatColor.GREEN + chesttype).toItemStack(), (e) -> {
                Player p = (Player) e.getWhoClicked();
                Menu menu = ChestsSetup.init(config, chesttype);
                menu.open(p);
            });
            slot += 1;
        }
        setItem(40, new ItemBuilder(XMaterial.BARRIER).setName("&cClose").toItemStack(), (e) -> {
            e.getWhoClicked().closeInventory();
        });
        setItem(44, new ItemBuilder(XMaterial.LIME_WOOL).setDurability(Short.parseShort("4")).setName("&aCreate a new chesttype!").toItemStack(), (e) -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            ChatSession sessions = new ChatSession(p);
            sessions.prompt(Arrays.asList(new String[] {"&aType the value you want: ", "&aType 'cancel' to cancel!"}), (input) -> {
                        if (input.equals("cancel")) {
                            sessions.end();
                            Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                    () -> {
                                        setItems(holder);
                                        p.openInventory(holder.getInventory());

                                    });

                            return;
                        }
                        sessions.end();
                        Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                () -> {
                                    Menu menu = ChestsSetup.init(config, input);
                                    menu.open(p);
                                });
                    });
        });
    }

    public static Menu init(ConfigWrapper config) {
        return new Browser(config);
    }
}
