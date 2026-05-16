package org.twightlight.skywars.fun.customitems;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.fun.customitems.assets.VoidBlock;
import org.twightlight.skywars.fun.customitems.commands.AutoItemCompleter;
import org.twightlight.skywars.fun.customitems.commands.Commands;
import org.twightlight.skywars.fun.customitems.listeners.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemsManager {
    private static Map<String, FunItem> registry = new HashMap<>();
    private static Logger LOGGER = SkyWars.LOGGER.getModule("FunItems");

    public static FunItem getItem(String string) {
        return registry.getOrDefault(string, null);
    }

    public static void register(String id, Class<? extends FunItem> clazz) {
        try {
            LOGGER.log(Logger.Level.INFO, "Loading item with id " + id + "...");
            registry.put(id, clazz.getConstructor(String.class).newInstance(id));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            LOGGER.log(Logger.Level.WARNING, "Failed to load item with id " + id + "...");
            return;
        }
    }

    public static void register(String id, FunItem funItem) {
        LOGGER.log(Logger.Level.INFO, "Loading item with id " + id + "...");
        registry.put(id, funItem);
    }

    public static void disable() {
        registry.clear();
    }

    public static List<String> getItems() {
        return new ArrayList<>(registry.keySet());
    }

    public static void load() {
        try {
            Class.forName("de.tr7zw.nbtapi.plugin.NBTAPI");
            LOGGER.log(Logger.Level.INFO, "NBTAPI found, loading items...");
            Bukkit.getPluginManager().registerEvents(new BlockBreak(), SkyWars.getInstance());

            Bukkit.getPluginManager().registerEvents(new DropAction(), SkyWars.getInstance());

            Bukkit.getPluginManager().registerEvents(new PlayerDamage(), SkyWars.getInstance());

            Bukkit.getPluginManager().registerEvents(new PlayerInteract(), SkyWars.getInstance());

            Bukkit.getPluginManager().registerEvents(new Armor(), SkyWars.getInstance());

            if (SkyWars.protocollib) {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();

                manager.addPacketListener(new PacketAdapter(SkyWars.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Client.HELD_ITEM_SLOT) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        int newSlot = event.getPacket().getIntegers().read(0);
                        int oldSlot = player.getInventory().getHeldItemSlot();

                        ItemStack oldItem = player.getInventory().getItem(oldSlot);
                        ItemStack newItem = player.getInventory().getItem(newSlot);

                        if (oldItem != null && getItem(oldItem) != null) {
                            boolean b = getItem(oldItem) != null;
                            if (b) {
                                getItem(oldItem).onUnholding(player, event, oldItem);
                            }
                        }
                        if (newItem != null) {
                            boolean b = getItem(newItem) != null;
                            if (b) {
                                getItem(newItem).onHolding(player, event, oldItem);
                            }
                        }
                    }
                });
            }
            loadItems();

            SkyWars.getInstance().getCommand("funitems").setExecutor(new Commands());
            SkyWars.getInstance().getCommand("funitems").setTabCompleter(new AutoItemCompleter());
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Logger.Level.WARNING, "NBTAPI not found, disabling FunItems.");
        }

    }

    private static void loadItems() {
        register("voidblock", VoidBlock.class);
    }

    public static FunItem getItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0) return null;
        String string = NBT.get(itemStack, nbt -> (String) nbt.getString("funitem"));
        return registry.getOrDefault(string, null);
    }
}
