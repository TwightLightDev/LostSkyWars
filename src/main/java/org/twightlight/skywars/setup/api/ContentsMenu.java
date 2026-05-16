package org.twightlight.skywars.setup.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ContentsMenu extends Menu {
    private static List<Integer> usableSlots = Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,
            28,29,30,31,32,33,34,37,38,39,40,41,42,43);
    private static List<Integer> decorationSlots = Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53);
    private static ItemStack decorator = BukkitUtils.createItem(Material.STAINED_GLASS_PANE, "", 1, 15, "", Collections.emptyList(), false);

    private ConfigWrapper config;
    private Consumer<Player> backAction;
    private String contentsPath;
    private List<ItemStack> contents;

    private ContentsMenu(ConfigWrapper config, Consumer<Player> backAction, String contentsPath) {
        super(54, false);

        this.config = config;
        this.contentsPath = contentsPath;
        this.contents = Optional.ofNullable(config.getStringList(contentsPath)).orElse(new ArrayList<>()).stream().map(BukkitUtils::fullyDeserializeItemStack).collect(Collectors.toList());
        this.backAction = backAction;

        setItems(holder, 1);
    }

    private void setItems(InventoryHolder holder, int page) {
        holder.getButtonsMap().clear();
        holder.getInventory().clear();

        int from = Math.min((page-1) * usableSlots.size(), contents.size());
        int to = Math.min(page * usableSlots.size(), contents.size());

        List<ItemStack> left = new ArrayList<>(contents.subList(0, from));
        List<ItemStack> usingContents = new ArrayList<>(contents.subList(from, to));
        List<ItemStack> right = new ArrayList<>(contents.subList(to, contents.size()));

        for (Integer i : decorationSlots) {
            setItem(i, decorator, null);
        }

        int min = Math.min(usableSlots.size(), usingContents.size());
        for (int i = 0; i < min ; i++) {
            holder.getInventory().setItem(usableSlots.get(i), usingContents.get(i));
        }

        setItem(49, BukkitUtils.createItem(Material.WOOL, "", 1, 5, "&aSave", Collections.emptyList(), false), (e) -> {
            Player p = (Player) e.getWhoClicked();
            List<ItemStack> newContents = new ArrayList<>();
            for (Integer i : usableSlots) {
                ItemStack is = holder.getInventory().getItem(i);
                if (is != null && is.getType() != Material.AIR) {
                    newContents.add(is);
                }
            }
            List<ItemStack> finalContents = new ArrayList<>();

            finalContents.addAll(left);
            finalContents.addAll(newContents);
            finalContents.addAll(right);
            config.set(contentsPath, finalContents.stream().map(BukkitUtils::fullySerializeItemStack).collect(Collectors.toList()));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully saved the contents"));
            if (backAction != null) {
                backAction.accept(p);
            }
        });

        setItem(53, BukkitUtils.createItem(Material.ARROW, "", 1, 0, "&aNext page", Collections.emptyList(), false), (e) -> {
            Player p = (Player) e.getWhoClicked();
            List<ItemStack> newContents = new ArrayList<>();
            for (Integer i : usableSlots) {
                ItemStack is = holder.getInventory().getItem(i);
                if (is != null && is.getType() != Material.AIR) {
                    newContents.add(is);
                }
            }
            List<ItemStack> finalContents = new ArrayList<>();

            finalContents.addAll(left);
            finalContents.addAll(newContents);
            finalContents.addAll(right);
            contents = finalContents;

            setItems(holder, page + 1);
            open(p);
        });
        if (page > 1) {
            setItem(45, BukkitUtils.createItem(Material.ARROW, "", 1, 0, "&aPrevious page", Collections.emptyList(), false), (e) -> {
                Player p = (Player) e.getWhoClicked();
                List<ItemStack> newContents = new ArrayList<>();
                for (Integer i : usableSlots) {
                    ItemStack is = holder.getInventory().getItem(i);
                    if (is != null && is.getType() != Material.AIR) {
                        newContents.add(is);
                    }
                }
                List<ItemStack> finalContents = new ArrayList<>();

                finalContents.addAll(left);
                finalContents.addAll(newContents);
                finalContents.addAll(right);
                contents = finalContents;

                setItems(holder, page - 1);
                open(p);
            });
        }
    }

    public static Menu init(ConfigWrapper config, Consumer<Player> backAction, String contentsPath) {
        return new ContentsMenu(config, backAction, contentsPath);
    }
}
