package org.twightlight.skywars.setup.chests.content_fills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.setup.InventoryHolder;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.setup.chests.Weights;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.ItemBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class Regular extends Menu {
    private ConfigUtils config;
    private Consumer<Player> backAction;
    private String path;
    private Regular(ConfigUtils config, Consumer<Player> backAction, String path) {
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
            for (String fill : elements) {
                setItem(slot, new ItemBuilder(XMaterial.CHEST).setName(ChatColor.GREEN + fill).toItemStack(), (e) -> {
                    Player p = (Player) e.getWhoClicked();
                    Menu menu = Weights.init(config, (p1) -> {
                        Menu menu1 = Regular.init(config, backAction, path);
                        menu1.open(p1);
                    }, path + "." + fill);
                    menu.open(p);
                });
                slot += 1;
            }
        }
        setItem(40, new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack(), (e) -> {
            backAction.accept((Player) e.getWhoClicked());
        } );
        setItem(44, new ItemBuilder(XMaterial.LIME_WOOL).setDurability(Short.parseShort("4")).setName("&aCreate a new fill pool!").toItemStack(), (e) -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            ChatSession sessions = new ChatSession(p);
            sessions.prompt(Arrays.asList(new String[] {"&aType the value you want: ", "&aType 'cancel' to cancel! The name should be 'fill_<integer>'!"}), (input) -> {
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
                            Menu menu = Weights.init(config, (p1) -> {
                                Menu menu1 = Regular.init(config, backAction, path);
                                menu1.open(p1);
                            }, path + "." + input);
                            menu.open(p);
                        });
            });
        });
    }

    public static Menu init(ConfigUtils config, Consumer<Player> backAction, String path) {
        return new Regular(config, backAction, path);
    }
}

