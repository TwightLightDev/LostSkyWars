package org.twightlight.skywars.commands.sw;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.arena.ui.cuboid.SkyWarsCube;
import org.twightlight.skywars.arena.ui.interfaces.ScanCallback;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.twightlight.skywars.listeners.player.PlayerInteractListener.CREATING;

public class CreateCommand extends SubCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length <= 1) {
            player.sendMessage("§cUse /lsw create <group> <name>");
            player.sendMessage("§7Available groups: " + GroupManager.getGroups());
            return;
        }

        if (Arena.getByWorldName(player.getWorld().getName()) != null) {
            player.sendMessage("§5[LostSkyWars] §cThis world already has an arena");
            return;
        }

        String groupId = args[0].toLowerCase();
        if (GroupManager.get(groupId) == null) {
            player.sendMessage("§cInvalid group! Available: " + GroupManager.getGroups());
            return;
        }

        String name = StringUtils.join(args, 1, " ");
        String[] array = new String[5];
        array[0] = groupId;
        array[1] = name;
        CREATING.put(player, array);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(2, BukkitUtils.deserializeItemStack("STICK : 1 : display=&aBorder"));
        player.getInventory().setItem(3, BukkitUtils.deserializeItemStack("STAINED_CLAY:5 : 1 : display=&aConfirm"));

        player.getInventory().setItem(5, BukkitUtils.deserializeItemStack("BED : 1 : display=&cCancel"));

        player.updateInventory();

        player.getInventory().setHeldItemSlot(4);
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage("§5[LostSkyWars] §aUse your hotbar items to create the game.");
    }

    @Override
    public String getUsage() {
        return "create <group> <name>";
    }

    @Override
    public String getDescription() {
        return "Create a new SkyWars game.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }

    public static void handleClick(Player player, Account account, String display, PlayerInteractEvent evt) {
        if (display.startsWith("§aBorder")) {
            evt.setCancelled(true);
            if (evt.getAction() == Action.LEFT_CLICK_BLOCK) {
                CREATING.get(player)[2] = BukkitUtils.serializeLocation(evt.getClickedBlock().getLocation());
                player.sendMessage("§5[LostSkyWars] §aBorder 1 setted!");
            } else if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                CREATING.get(player)[3] = BukkitUtils.serializeLocation(evt.getClickedBlock().getLocation());
                player.sendMessage("§5[LostSkyWars] §aBorder 2 setted!");
            } else {
                player.sendMessage("§5[LostSkyWars] §cClick in a block.");
            }
        } else if (display.startsWith("§aConfirm")) {
            evt.setCancelled(true);
            String[] array = CREATING.get(player);
            if (array[2] == null) {
                player.sendMessage("§5[LostSkyWars] §cSet the border 1 using the left click.");
                return;
            }

            if (array[3] == null) {
                player.sendMessage("§5[LostSkyWars] §cSet the border 2 using the right click.");
                return;
            }

            World world = player.getWorld();
            ConfigWrapper config = ConfigWrapper.getConfig(world.getName(), "plugins/LostSkyWars/servers");
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.updateInventory();
            CREATING.remove(player);
            player.sendMessage("§5[LostSkyWars] §aCreating \"" + array[0] + "\" arena...");

            String groupId = array[0];
            boolean no_spawn = GroupManager.get(groupId) != null && GroupManager.get(groupId).hasTrait("no_spawn");

            SkyWarsCube sc = new SkyWarsCube(BukkitUtils.deserializeLocation(array[2]), BukkitUtils.deserializeLocation(array[3]));
            List<String> chests = new ArrayList<>(), spawns = new ArrayList<>();
            for (Iterator<Block> itr = sc.iterator(); itr.hasNext(); ) {
                Block block = itr.next();
                if (block.getType() == Material.CHEST) {
                    chests.add(BukkitUtils.serializeLocation(block.getLocation()) + "; solo");
                } else if (block.getType() == Material.BEACON) {
                    if (no_spawn && spawns.size() > 1) {
                        continue;
                    }
                    block.setType(Material.AIR);
                    spawns.add(BukkitUtils.serializeLocation(block.getLocation().clone().add(0.5, 0, 0.5)));
                }
            }
            player.sendMessage("§5[LostSkyWars] §aScanned " + spawns.size() + " spawns & " + chests.size() + " chests.");

            config.set("name", array[1]);
            config.set("group", groupId);
            config.set("cube", sc.toString());
            config.set("min-players", spawns.size() / 2 > 0 ? spawns.size() / 2 : 2);
            config.set("spawns", spawns);
            config.set("chests", chests);
            config.set("balloons", new ArrayList<>());
            world.save();

            Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> player.sendMessage("§5[LostSkyWars] §aCreating backup of world..."), 20);

            Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
                if (!SkyWars.getInstance().getWorldLoader().importWorld(world)) {
                    player.sendMessage("§cFailed to import world (see the console)!");
                }
                try {
                    account.refreshPlayer();
                    player.sendMessage("§5[LostSkyWars] §aAdding to arena list...");
                    Arena.loadArena(config.getFile(), new ScanCallback() {

                        @Override
                        public void finish() {
                            if (player.isOnline()) {
                                player.sendMessage("§5[LostSkyWars] §aGame created successfully!");
                            }
                        }
                    });
                } catch (Exception ex) {
                    Arena.LOGGER.log(Level.WARNING, "Cannot load a new game: ", ex);
                    if (player.isOnline()) {
                        player.sendMessage("§5[LostSkyWars] §cCannot load a new game (see the console)");
                    }
                    account.refreshPlayer();
                }
            }, 60);
        } else if (display.startsWith("§cCancel")) {
            evt.setCancelled(true);
            CREATING.remove(player);
            account.refreshPlayer();
            player.sendMessage("§5[LostSkyWars] §aGame creation cancelled.");
        }
    }
}
