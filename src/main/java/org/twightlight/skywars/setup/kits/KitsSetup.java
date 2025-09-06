package org.twightlight.skywars.setup.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.setup.ContentsMenu;
import org.twightlight.skywars.setup.InventoryHolder;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.StringCheckerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KitsSetup extends Menu {

    private ConfigUtils config;
    private String path;
    private final List<String> rarities = Arrays.stream(CosmeticRarity.values())
            .map(CosmeticRarity::getUncoloredName)
            .collect(Collectors.toList());

    private KitsSetup(ConfigUtils config, String path) {
        super(45, true);
        this.config = config;
        this.path = path;
        holder.setCloseExecutable((e) -> {
            if (!config.contains(path + ".permission")) {
                config.setNotSave(path + ".permission", "none");
            }
            if (!config.contains(path + ".rarity")) {
                config.setNotSave(path + ".rarity", "COMMON");
            }
            if (!config.contains(path + ".price")) {
                config.setNotSave(path + ".price", 100);
            }
            if (!config.contains(path + ".icon")) {
                config.setNotSave(path + ".icon", new ItemStack(Material.BEDROCK, 1));
            }
            if (!config.contains(path + ".content")) {
                config.setNotSave(path + ".content", Collections.emptyList());
            }
            if (!config.contains(path + ".potion-effects")) {
                config.setNotSave(path + ".potion-effects", Collections.emptyList());
            }
            if (!config.contains(path + ".armor")) {
                ItemStack air = new ItemStack(Material.AIR, 1);
                config.setNotSave(path + ".armor", new ArrayList<>(Collections.nCopies(4, BukkitUtils.fullySerializeItemStack(air))));
            }

            config.save();
        });
        setItems(holder);
    }

    private void setItems(InventoryHolder holder) {
        holder.getButtonsMap().clear();
        holder.getInventory().clear();

        setItem(11, BukkitUtils.createItem(Material.PAPER, "", 1, 0, "&bId", Arrays.asList("&fCurrent value: &e" + config.getInt(path + ".id", 0), "", "&eClick to change the value!"), false),
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
                                    config.setNotSave(path + ".id", Integer.valueOf(input));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });
        setItem(12, BukkitUtils.createItem(Material.NAME_TAG, "", 1, 0, "&bName", Arrays.asList("&fCurrent value: &e" + config.getString(path + ".name", "none"), "", "&eClick to change the value!"), false),
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
        setItem(13, BukkitUtils.createItem(Material.PAPER, "", 1, 0, "&bPrice", Arrays.asList("&fCurrent value: &e" + config.getInt(path + ".price", 0), "", "&eClick to change the value!"), false),
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
                                    config.setNotSave(path + ".price", Integer.valueOf(input));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });
        setItem(14, BukkitUtils.createItem(Material.DIAMOND, "", 1, 0, "&bRarity", Arrays.asList("&fCurrent value: &e" + config.getString(path + ".rarity", "none"), "", "&eClick to switch the value!"), false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!rarities.contains(config.getString(path + ".rarity", "null"))) {
                        config.setNotSave(path + ".rarity", rarities.get(0));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set new rarity to: &e" + rarities.get(0)));
                        setItems(holder);
                        p.openInventory(holder.getInventory());

                    } else {
                        int i = rarities.indexOf(config.getString(path + ".rarity", "null"));
                        if (i >= rarities.size()-1) {
                            i = -1;
                        }
                        config.setNotSave(path + ".rarity", rarities.get(i+1));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set new rarity to: &e" + rarities.get(i+1)));
                        setItems(holder);
                        p.openInventory(holder.getInventory());

                    }
                });
        setItem(15, BukkitUtils.createItem(Material.COMMAND, "", 1, 0, "&bPermission", Arrays.asList("&fCurrent value: &e" + config.getString(path + ".permission", "none"), "", "&eClick to change the value!"), false),
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
                                    config.setNotSave(path + ".permission", input);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set value to: &e" + input));
                                    setItems(holder);
                                    p.openInventory(holder.getInventory());
                                });
                    });
                });

        ItemStack icon = BukkitUtils.fullyDeserializeItemStack(config.getString(path + ".icon", null));
        ItemStack preicon;
        if (icon.getType() == Material.AIR) {
            icon = BukkitUtils.createItem(Material.BEDROCK, "", 1, 0, "&cIcon Not Set", Arrays.asList("&eClick to set to your current holding item!"), false);
            preicon = icon.clone();
        } else {
            preicon = icon.clone();
            ItemMeta meta = icon.getItemMeta();
            String displayname = meta.getDisplayName();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&' ,"&bIcon: &r" + displayname));
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&eLeft-Click to set to your holding item!"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&eRight-Click to get current icon!"));
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        setItem(21, icon,
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (e.isLeftClick()) {
                        ItemStack itemStack = p.getItemInHand();
                        if (itemStack.getType() == Material.AIR) {
                            config.setNotSave(path + ".icon", null);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully removed kit's icon"));
                            setItems(holder);
                            p.openInventory(holder.getInventory());

                        } else {
                            config.setNotSave(path + ".icon", BukkitUtils.fullySerializeItemStack(itemStack));
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully updated kit's icon"));
                            setItems(holder);
                            p.openInventory(holder.getInventory());
                        }
                    } else if (e.isRightClick() && !preicon.getItemMeta().getDisplayName().equals("&cIcon Not Set")) {
                        p.getInventory().addItem(preicon);
                    }
                });
        setItem(22, BukkitUtils.createItem(Material.IRON_CHESTPLATE, "", 1, 0, "&bArmors", Arrays.asList("" ,"&eLeft-Click to set to your current armors!", "&eRight-Click to get current armors!"), false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (e.isLeftClick()) {
                        List<String> list = Arrays.stream(p.getInventory().getArmorContents()).map(BukkitUtils::fullySerializeItemStack).collect(Collectors.toList());
                        config.setNotSave(path + ".armor", list);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully updated kit's armors"));
                        setItems(holder);
                        p.openInventory(holder.getInventory());
                    } else if (e.isRightClick()) {
                        p.getInventory().setArmorContents(deserializeArmor(config.getStringList(path + ".armor")));
                        p.closeInventory();
                        p.sendMessage("Succesfully imported kit's armors!");
                    }

        });
        setItem(23, BukkitUtils.createItem(Material.CHEST, "", 1, 0, "&bContents", Arrays.asList("" ,"&eClick to browse!"), false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    Menu menu = ContentsMenu.init(config, (player) -> {
                        Menu menu1 = KitsSetup.init(config, path);
                        menu1.open(player);
                    }, path + ".content");
                    menu.open(p);
                });

        List<String> lore = config.getStringList(path + ".potion-effects");
        lore.add("");
        lore.add("&eLeft-Click to add a potion effect");
        lore.add("&eRight-Click to remove the last potion-effect");
        setItem(31, BukkitUtils.createItem(Material.POTION, "", 1, 0, "&bPotion Effect", lore, false),
                (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (e.isLeftClick()) {
                        p.closeInventory();
                        ChatSession sessions = new ChatSession(p);
                        sessions.prompt(Arrays.asList(new String[] {"&aType the value you want: ", "&aType 'cancel' to cancel!", "&aThe format should be '<Potion> : <Duration> : <Amplifier> : <Ambient> : <hasParticle>'"}), (input) -> {
                            if (input.equals("cancel")) {
                                sessions.end();
                                Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                        () -> {
                                            setItems(holder);
                                            p.openInventory(holder.getInventory());

                                        });
                                return;
                            } else if (BukkitUtils.deserializePotionEffect(input) == null) {
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
                                        List<String> lore1 = config.getStringList(path + ".potion-effects");
                                        lore1.add(input);
                                        config.setNotSave(path + ".potion-effects", lore1);
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully add a new potion effect"));
                                        setItems(holder);
                                        open(p);
                                    });
                        });
                    } else if (e.isRightClick()) {
                        Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                () -> {
                                    List<String> lore1 = config.getStringList(path + ".potion-effects");
                                    lore1.remove(lore1.size()-1);
                                    config.setNotSave(path + ".potion-effects", lore1);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully remove a potion effect"));
                                    setItems(holder);
                                    open(p);
                                });
                    }
                });
    }

    public static Menu init(ConfigUtils config, String path) {
        return new KitsSetup(config, path);
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
