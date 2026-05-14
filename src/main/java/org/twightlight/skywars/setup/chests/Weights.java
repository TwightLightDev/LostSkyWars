package org.twightlight.skywars.setup.chests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.setup.ContentsMenu;
import org.twightlight.skywars.setup.InventoryHolder;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.config.ConfigUtils;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.StringCheckerUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class Weights extends Menu {
    private ConfigUtils config;
    private Consumer<Player> backAction;
    private String path;
    private Weights(ConfigUtils config, Consumer<Player> backAction, String path) {
        super(45, true);
        this.config = config;
        this.backAction = backAction;
        this.path = path;
        holder.setCloseExecutable((e) -> {
            config.save();
        });

        setItems(holder);
    }

    private void setItems(InventoryHolder holder) {
        holder.getButtonsMap().clear();
        holder.getInventory().clear();
        int slot = 0;
        if (config.getSection(path) != null) {
            Set<String> elements = Optional.ofNullable(config.getSection(path).getKeys(false)).orElse(new HashSet<>());
            for (String weight : elements) {
                setItem(slot, new ItemBuilder(XMaterial.ANVIL).setName(ChatColor.GREEN + weight).toItemStack(), (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    Menu menu = ContentsMenu.init(config, (p1) -> {
                        Menu menu1 = Weights.init(config, backAction, path);
                        menu1.open(p1);
                    }, path + "." + weight);
                    menu.open(p);
                });
                slot += 1;
            }
        }
        setItem(40, new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack(), (e) -> {
            backAction.accept((Player) e.getWhoClicked());
        } );
        setItem(44, new ItemBuilder(XMaterial.LIME_WOOL).setDurability(Short.parseShort("4")).setName("&aCreate a new weight group!").toItemStack(), (e) -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            ChatSession sessions = new ChatSession(p);
            sessions.prompt(Arrays.asList(new String[] {"&aType the integer value you want: ", "&aType 'cancel' to cancel!!"}), (input) -> {
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
                            Menu menu = ContentsMenu.init(config, (p1) -> {
                                Menu menu1 = Weights.init(config, backAction, path);
                                menu1.open(p1);
                            }, path + "." + input);
                            menu.open(p);
                        });
            });
        });
    }

    public static Menu init(ConfigUtils config, Consumer<Player> backAction, String path) {
        return new Weights(config, backAction, path);
    }
}


