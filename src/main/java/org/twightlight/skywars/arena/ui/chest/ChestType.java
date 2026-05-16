package org.twightlight.skywars.arena.ui.chest;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.math.NumberUtils;
import org.twightlight.skywars.utils.string.StringCheckerUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ChestType {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final List<Integer> SLOTS = new ArrayList<>();

    static {
        for (int slot = 0; slot < 27; slot++) {
            SLOTS.add(slot);
        }
    }
    private String name;
    private Map<Integer, List<ChestItem>> contents;
    private Map<Integer, List<Integer>> contentInts = new HashMap<>();
    private List<ChestItem> guaranteed;
    private int minItems;
    private int maxItems;

    public ChestType(String name, Map<Integer, List<ChestItem>> contents, List<ChestItem> guaranteed, int minItems, int maxItems) {
        this.name = name;
        this.contents = contents;
        this.minItems = minItems;
        this.maxItems = maxItems;
        this.guaranteed = guaranteed;
        for (int id : contents.keySet()) {
            contentInts.computeIfAbsent(id, (id1) -> {
                return new ArrayList<>();
            }).add(0);

            for (ChestItem item : contents.get(id)) {
                int last = contentInts.get(id).get(contentInts.get(id).size()-1);
                contentInts.get(id).add(last + item.getWeight());
            }
        }

    }

    public void fill(Location location, int fillCount) {
        Block block = location.getBlock();
        if (block == null || !(block.getState() instanceof Chest)) {
            block.setType(Material.CHEST);
        }
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();

            Inventory inventory = chest.getInventory();
            inventory.clear();
            int itemCount = RANDOM.nextInt(minItems, maxItems + 1);
            Collections.shuffle(SLOTS);
            int index = 0;

            List<ItemStack> itemsToPlace = new ArrayList<>();
            guaranteed.forEach((ci) -> {
                itemsToPlace.add(ci.getItem());
            });
            int pointer = fillCount;
            while (contents.get(fillCount) == null && pointer > 0) {
                pointer -= 1;
            }
            List<ChestItem> target = Optional.ofNullable(contents.get(pointer)).orElse(new ArrayList<>());
            List<Integer> targetInts = Optional.ofNullable(contentInts.get(pointer)).orElse(new ArrayList<>());

            int bound = targetInts.get(targetInts.size()-1);
            if (bound <= 0 || target.isEmpty()) {
                LOGGER.log(Logger.Level.WARNING, "ChestType \"" + name + "\" has no valid items to choose from");
                return;
            }
            while (itemsToPlace.size() < itemCount) {
                int random = RANDOM.nextInt(bound);
                int res = NumberUtils.closestGreater(targetInts, random);
                if (res <= 0 || res > target.size()) continue;
                itemsToPlace.add(target.get(res-1).getItem());
            }

            Collections.shuffle(itemsToPlace);

            while (index < itemCount && !itemsToPlace.isEmpty()) {
                ItemStack itemToPlace = itemsToPlace.remove(0);
                if (itemToPlace != null) {
                    inventory.setItem(SLOTS.get(index++), itemToPlace);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("ChestType");
    private static Map<String, ChestType> types = new HashMap<>();

    public static void setupTypes() {
        ConfigWrapper cu = ConfigWrapper.getConfig("chesttypes");

        for (String key : cu.getKeys(false)) {
            String name = cu.getString(key + ".name");

            Map<Integer, List<ChestItem>> contents = new HashMap<>();

            Set<String> contentSections = cu.getSection(key + ".contents").getKeys(false);
            if (contentSections != null) {
                for (String section : contentSections) {
                    if (!StringCheckerUtils.isInteger(section.replace("fill_", ""))) {
                        LOGGER.log(Logger.Level.WARNING, "Invalid fill id key: \"" + section + "\"");
                    }

                    int id = Integer.parseInt(section.replace("fill_", ""));

                    ConfigurationSection weightSection = cu.getSection(key + ".contents." + section);

                    for (String weightStr : weightSection.getKeys(false)) {
                        int weight;
                        try {
                            weight = Integer.parseInt(weightStr);
                            if (weight == 0) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Logger.Level.WARNING, "Invalid content weight key: \"" + weightStr + "\" in " + name);
                            continue;
                        }

                        List<String> entries = weightSection.getStringList(weightStr);
                        for (String itemStr : entries) {
                            try {
                                contents.computeIfAbsent(id, (k) -> {
                                    return new ArrayList<>();
                                }).add(new ChestItem(BukkitUtils.fullyDeserializeItemStack(itemStr), weight));
                            } catch (Exception e) {
                                LOGGER.log(Logger.Level.WARNING, "Invalid ContentItem (name=\"" + name + "\", string=\"" + itemStr + "\")");
                            }
                        }
                    }
                }
            }
            List<ChestItem> guaranteed = new ArrayList<>();
            if (cu.contains(key + ".guaranteed_contents")) {
                for (String gi : cu.getStringList(key + ".guaranteed_contents")) {
                    guaranteed.add(new ChestItem(BukkitUtils.fullyDeserializeItemStack(gi), 0));
                }
            }


            int minItems = cu.getInt(key + ".minItems", 1);
            int maxItems = cu.getInt(key + ".maxItems", 27);

            types.put(name.toLowerCase(), new ChestType(name.toLowerCase(), contents, guaranteed, minItems, maxItems));
        }

        LOGGER.log(Logger.Level.INFO, "Loaded " + types.size() + " chesttypes!");
    }

    public static ChestType getFirst() {
        return types.values().stream().findFirst().orElse(null);
    }

    public static ChestType getByName(String name) {
        return types.get(name.toLowerCase());
    }

    public static Collection<ChestType> listTypes() {
        return ImmutableList.copyOf(types.values());
    }
}
