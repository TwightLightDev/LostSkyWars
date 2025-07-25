package org.twightlight.skywars.modules.recentgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.modules.recentgames.GameResult;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.modules.recentgames.User;
import org.twightlight.skywars.modules.recentgames.menus.Item;
import org.twightlight.skywars.modules.recentgames.menus.RGMenu;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.Logger.Level;

import java.util.List;
import java.util.stream.Collectors;

public class RecentGamesCommand extends Command {

    public RecentGamesCommand() {
        super("recentgames");

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            RGMenu menu = RGMenu.createMenu();
            final int[] page = {1};
            int amountPerPage = 4;
            int[] slots = new int[] {10, 12, 14, 16};
            List<GameData> dataList = User.getUser(player).getData();
            Item close = new Item((e) -> {
                e.getWhoClicked().closeInventory();
            }, (p) -> {
                return new ItemBuilder(XMaterial.BARRIER).setName("&cClose").toItemStack();
            });
            menu.addContent(31, close);
            if (dataList.isEmpty()) {
                Item noGame = new Item((e) -> {
                    return;
                }, (p) -> {
                    return new ItemBuilder(XMaterial.BEDROCK).setName("&cYou don't have any recent game!").toItemStack();
                });
                menu.addContent(13, noGame);
                menu.open(player);
                return true;
            }
            Item nextPage = new Item((e) -> {
                page[0] += 1;
                addContents(menu, page[0], amountPerPage, dataList, slots);
                menu.open((Player) e.getWhoClicked());
            }, (p) -> {
                if (page[0] * amountPerPage > dataList.size()) {
                    return new ItemStack(Material.AIR);
                }
                return new ItemBuilder(XMaterial.ARROW).setName("&aNext page").toItemStack();
            });
            menu.addContent(35, nextPage);
            Item previousPage = new Item((e) -> {
                if (page[0] > 1) {
                    page[0] -= 1;
                }
                addContents(menu, page[0], amountPerPage, dataList, slots);
                menu.open((Player) e.getWhoClicked());
            }, (p) -> {
                if (page[0] == 1) {
                    return new ItemStack(Material.AIR);
                }
                return new ItemBuilder(XMaterial.ARROW).setName("&aPrevious page").toItemStack();
            });
            menu.addContent(27, previousPage);

            addContents(menu, page[0], amountPerPage, dataList, slots);

            menu.open(player);
        }

        return true;
    }

    private void addContents(RGMenu menu, int page, int amountPerPage, List<GameData> dataList, int[] slots) {
        for (int i = 0 ; i < amountPerPage ; i++)  {
            int index = (page - 1) * amountPerPage + i;
            Item paper = new Item((e) -> {
                if (RecentGames.hasReplayHook()) {
                    RecentGames.getReplayHook().play(dataList.get(index), (Player) e.getWhoClicked());
                }
            }, (p) -> {
                if (index > dataList.size() - 1) {
                    return new ItemStack(Material.AIR);

                }
                return new ItemBuilder(XMaterial.PAPER).setName("&f" + dataList.get(index).getName()).
                        setLore(Language.recentgames$lore.stream().map((line) -> line.replace("{result}", (dataList.get(index).getResult() == GameResult.WIN) ? "VICTORY" : (dataList.get(index).getResult() == GameResult.LOSE) ? "DEFEAT" : "UNKNOWN")
                                .replace("{startTime}", dataList.get(index).getFormattedStartTime()).replace("{duration}", dataList.get(index).getFormattedDuration())).collect(Collectors.toList())).toItemStack();
            });

            menu.addContent(slots[i], paper);
        }
    }
}
