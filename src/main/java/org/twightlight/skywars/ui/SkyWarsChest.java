package org.twightlight.skywars.ui;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.holograms.Hologram;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;
import org.twightlight.skywars.utils.NumberUtils;
import org.twightlight.skywars.world.WorldServer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SkyWarsChest {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final List<Integer> SLOTS = new ArrayList<>();

    static {
        for (int slot = 0; slot < 27; slot++) {
            SLOTS.add(slot);
        }
    }

    private WorldServer<?> server;
    private String serialized;
    private String chestType;

    private Hologram hologram = null;

    public SkyWarsChest(WorldServer<?> server, String serialized) {
        this.server = server;
        this.serialized = serialized;
        this.chestType = serialized.split("; ")[6];
    }

    public void update() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram != null) {
            Block block = this.getLocation().getBlock();
            if (!(block.getState() instanceof Chest)) {
                this.destroy();
                return;
            }

            NMS.playChestAction(this.getLocation(), true);
            if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Refill) {
                this.hologram.updateLine(1, Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
            } else if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Doom) {
                this.hologram.updateLine(1, Language.game$hologram$no_refill);
            }
        }
    }

    public void createHologram() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram == null) {
            this.hologram = Holograms.createHologram(this.getLocation().add(0.5, -0.5, 0.5));
            if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Refill) {
                this.hologram.withLine(Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
            } else if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Doom) {
                this.hologram.withLine(Language.game$hologram$no_refill);
            }
        }
    }

    public void destroy() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram != null) {
            NMS.playChestAction(this.getLocation(), false);
            Holograms.removeHologram(this.hologram);
            this.hologram = null;
        }
    }

    public void setType(ChestType chestType) {
        this.chestType = chestType.getName();
    }

    public void fill() {
        ChestType type = ChestType.getByName(chestType);
        if (type == null) {
            type = ChestType.getFirst();
        }

        if (type != null) {
            type.fill(getLocation(), false);
        }
    }

    public void refill() {
        ChestType type = ChestType.getByName(chestType);
        if (type == null) {
            type = ChestType.getFirst();
        }

        if (type != null) {
            type.fill(getLocation(), true);
        }
    }

    public String getChestType() {
        return chestType;
    }

    public Location getLocation() {
        return BukkitUtils.deserializeLocation(serialized, server);
    }

    @Override
    public String toString() {
        return BukkitUtils.serializeLocation(getLocation()) + "; " + chestType;
    }

    public static class ChestType {

        private String name;
        private List<ChestItem> refill;
        private List<ChestItem> content;
        private List<Integer> contentInts = new ArrayList<>();
        private List<Integer> refillInts = new ArrayList<>();
        private int minItems;
        private int maxItems;

        public ChestType(String name, List<ChestItem> refill, List<ChestItem> content, int minItems, int maxItems) {
            this.name = name;
            this.refill = refill;
            this.content = content;
            this.minItems = minItems;
            this.maxItems = maxItems;
            contentInts.add(0);
            refillInts.add(0);
            for (ChestItem item : refill) {
                int last = refillInts.get(refillInts.size()-1);
                refillInts.add(last + item.getPercentage());
            }
            for (ChestItem item : content) {
                int last = contentInts.get(contentInts.size()-1);
                contentInts.add(last + item.getPercentage());
            }
        }

        public void fill(Location location, boolean refill) {
            Block block = location.getBlock();
            if (block != null && block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();

                Inventory inventory = chest.getInventory();
                inventory.clear();
                int itemCount = RANDOM.nextInt(minItems, maxItems + 1);
                Collections.shuffle(SLOTS);
                int index = 0;

                List<ItemStack> itemsToPlace = new ArrayList<>();
                List<ChestItem> target = refill ? this.refill : this.content;
                List<Integer> targetInts = refill ? this.refillInts : this.contentInts;

                int bound = targetInts.get(targetInts.size()-1);
                if (bound <= 0 || target.isEmpty()) {
                    LOGGER.log(Level.WARNING, "ChestType \"" + name + "\" has no valid items to choose from");
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

        public static final Logger LOGGER = Main.LOGGER.getModule("ChestType");
        private static Map<String, ChestType> types = new HashMap<>();

        public static void setupTypes() {
            ConfigUtils cu = ConfigUtils.getConfig("chesttypes");

            for (String key : cu.getKeys(false)) {
                String name = cu.getString(key + ".name");

                List<ChestItem> citems = new ArrayList<>();
                List<ChestItem> ritems = new ArrayList<>();

                ConfigurationSection contentSection = cu.getSection(key + ".content");
                if (contentSection != null) {
                    for (String percentageStr : contentSection.getKeys(false)) {
                        int percentage;
                        try {
                            percentage = Integer.parseInt(percentageStr);
                            if (percentage == 0) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid content percentage key: \"" + percentageStr + "\" in " + name);
                            continue;
                        }

                        List<String> entries = contentSection.getStringList(percentageStr);
                        for (String itemStr : entries) {
                            try {
                                citems.add(new ChestItem(BukkitUtils.fullyDeserializeItemStack(itemStr), percentage));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Invalid ContentItem (name=\"" + name + "\", string=\"" + itemStr + "\")");
                            }
                        }
                    }
                }

                ConfigurationSection refillSection = cu.getSection(key + ".refill");
                if (refillSection != null) {
                    for (String percentageStr : refillSection.getKeys(false)) {
                        int percentage;
                        try {
                            percentage = Integer.parseInt(percentageStr);
                            if (percentage == 0) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid refill percentage key: \"" + percentageStr + "\" in " + name);
                            continue;
                        }

                        List<String> entries = refillSection.getStringList(percentageStr);
                        for (String itemStr : entries) {
                            try {
                                ritems.add(new ChestItem(BukkitUtils.fullyDeserializeItemStack(itemStr), percentage));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Invalid RefillItem (name=\"" + name + "\", string=\"" + itemStr + "\")");
                            }
                        }
                    }
                }

                int minItems = cu.getInt(key + ".minItems", 1);
                int maxItems = cu.getInt(key + ".maxItems", 27);

                types.put(name.toLowerCase(), new ChestType(name.toLowerCase(), ritems, citems, minItems, maxItems));
            }

            LOGGER.log(Level.INFO, "Loaded " + types.size() + " chesttypes!");
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

    public static class ChestItem {

        private ItemStack item;
        private int percentage;

        public ChestItem(ItemStack item, int percentage) {
            this.item = item;
            this.percentage = percentage;
        }

        public int getPercentage() {
            return percentage;
        }

        public ItemStack getItem() {
            return item;
        }
    }
}
