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
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.modules.recentgames.GameResult;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.modules.recentgames.User;
import org.twightlight.skywars.modules.recentgames.menus.RGMenu;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.Logger.Level;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecentGamesCommand extends Command {

    public RecentGamesCommand() {
        super("recentgames");
        this.setAliases(Arrays.asList("rg", "recentgame"));
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
            List<Integer> slots = Arrays.stream(RecentGames.getMenuConfig().getString("menu.paginated-slot").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            List<GameData> dataList = User.getUser(player).getData();
            Item close = new Item((e) -> {
                e.getWhoClicked().closeInventory();
            }, (p) -> {
                return ItemBuilder.parse(RecentGames.getMenuConfig().getYml(), "recentgames.menu.items.close").toItemStack();
            });
            menu.setItem(RecentGames.getMenuConfig().getInt("menu.items.close.slot"), close);
            if (dataList.isEmpty()) {
                Item noGame = new Item((e) -> {
                    return;
                }, (p) -> {
                    return ItemBuilder.parse(RecentGames.getMenuConfig().getYml(), "recentgames.menu.items.no-game").toItemStack();
                });
                menu.setItem(RecentGames.getMenuConfig().getInt("menu.items.no-game.slot"), noGame);
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
                return ItemBuilder.parse(RecentGames.getMenuConfig().getYml(), "recentgames.menu.items.next-page").toItemStack();
            });
            menu.setItem(RecentGames.getMenuConfig().getInt("menu.items.next-page.slot"), nextPage);
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
                return ItemBuilder.parse(RecentGames.getMenuConfig().getYml(), "recentgames.menu.items.previous-page").toItemStack();
            });
            menu.setItem(RecentGames.getMenuConfig().getInt("menu.items.previous-page.slot"), previousPage);

            addContents(menu, page[0], amountPerPage, dataList, slots);

            menu.open(player);
        }

        return true;
    }

    private void addContents(RGMenu menu, int page, int amountPerPage, List<GameData> dataList, List<Integer> slots) {
        for (int i = 0 ; i < amountPerPage ; i++)  {
            int index = (page - 1) * amountPerPage + i;
            Item paper = new Item((e) -> {
                if (RecentGames.hasReplayHook()) {
                    RecentGames.getReplayHook().play(dataList.get(index), User.getUser((Player) e.getWhoClicked()));
                }
            }, (p) -> {
                if (index > dataList.size() - 1) {
                    return new ItemStack(Material.AIR);

                }
                return new ItemBuilder(XMaterial.valueOf(RecentGames.getMenuConfig().getString("menu.items.game-item.material", "PAPER"))).
                        setName(RecentGames.getMenuConfig().getString("menu.items.game-item.name").replace("{arena}", dataList.get(index).getName())).
                        setLore(RecentGames.getMenuConfig().getList("menu.items.game-item.lore").stream().map((line) -> line.replace("{result}", (dataList.get(index).getResult() == GameResult.WIN) ? "VICTORY" : (dataList.get(index).getResult() == GameResult.LOSE) ? "DEFEAT" : "UNKNOWN")
                                .replace("{startTime}", dataList.get(index).getFormattedStartTime()).replace("{duration}", dataList.get(index).getFormattedDuration())).collect(Collectors.toList())).toItemStack();
            });

            menu.setItem(slots.get(i), paper);
        }
    }
}
