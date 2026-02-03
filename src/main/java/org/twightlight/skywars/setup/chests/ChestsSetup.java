package org.twightlight.skywars.setup.chests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.setup.InventoryHolder;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.setup.chests.content_fills.Guaranteed;
import org.twightlight.skywars.setup.chests.content_fills.Regular;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.StringCheckerUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChestsSetup extends Menu {

    private ConfigUtils config;
    private String path;

    private ChestsSetup(ConfigUtils config, String path) {
        super(45, true);
        this.config = config;
        this.path = path;
        holder.setCloseExecutable((e) -> {
            if (!config.contains(path + ".name")) {
                config.setNotSave(path + ".name", "undefined");
            }
            if (!config.contains(path + ".minItems")) {
                config.setNotSave(path + ".minItems", 5);
            }
            if (!config.contains(path + ".maxItems")) {
                config.setNotSave(path + ".maxItems", 10);
            }

            config.save();
        });
        setItems(holder);
    }

    private void setItems(InventoryHolder holder) {
        holder.getButtonsMap().clear();
        holder.getInventory().clear();

        setItem(10, BukkitUtils.createItem(Material.NAME_TAG, "", 1, 0, "&bName", Arrays.asList("&fCurrent value: &e" + config.getString(path + ".name", "none"), "", "&eClick to change the value!"), false),
                (e) -> {
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
                                    config.setNotSave(path + ".name", input);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });
        setItem(11, BukkitUtils.createItem(Material.IRON_INGOT, "", 1, 0, "&bMin Items",
                        Arrays.asList("&fCurrent value: &e" + config.getInt(path + ".minItems", 0), "", "&eClick to change the value!"), false),
                (e) -> {
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
                        } else if (!StringCheckerUtils.isInteger(input)) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Value! Cancel the action!"));
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
                                    config.setNotSave(path + ".minItems", Integer.valueOf(input));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });
        setItem(12, BukkitUtils.createItem(Material.GOLD_INGOT, "", 1, 0, "&bMax Items",
                        Arrays.asList("&fCurrent value: &e" + config.getInt(path + ".maxItems", 0), "", "&eClick to change the value!"), false),
                (e) -> {
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
                        } else if (!StringCheckerUtils.isInteger(input)) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Value! Cancel the action!"));
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
                                    config.setNotSave(path + ".maxItems", Integer.valueOf(input));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });
        setItem(13, BukkitUtils.createItem(Material.CHEST, "", 1, 0, "&bContents", Arrays.asList("" ,"&eClick to browse!"), false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    Menu menu = Regular.init(config, (player) -> {
                        Menu menu1 = ChestsSetup.init(config, path);
                        menu1.open(player);
                    }, path + ".contents");
                    menu.open(p);
                });
        setItem(14, BukkitUtils.createItem(Material.CHEST, "", 1, 0, "&bGuaranteed Contents", Arrays.asList("" ,"&eClick to browse!"), false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    Menu menu = Guaranteed.init(config, (player) -> {
                        Menu menu1 = ChestsSetup.init(config, path);
                        menu1.open(player);
                    }, path + ".guaranteed_contents");
                    menu.open(p);
                });

        setItem(40, new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack(), (e) -> {
            Browser.init(config).open((Player) e.getWhoClicked());
        } );

    }

    public static Menu init(ConfigUtils config, String path) {
        return new ChestsSetup(config, path);
    }


    public ItemStack[] deserializeArmor(List<String> serializedList) {
        ItemStack[] armor = new ItemStack[4];

        for (int i = 0; i < armor.length && i < serializedList.size(); i++) {
            String base64 = serializedList.get(i);
            armor[i] = BukkitUtils.fullyDeserializeItemStack(base64);
        }

        for (int i = 0; i < armor.length; i++) {
            if (armor[i] == null) armor[i] = new ItemStack(Material.AIR);
        }

        return armor;
    }
}
