package org.twightlight.skywars.cosmetics.visual.categories;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.*;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.hook.PacketEventsHook;
import org.twightlight.skywars.hook.worldedit.WorldEditHook;
import org.twightlight.skywars.setup.cage.CageSetupSession;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class SkyWarsCage extends PreviewableCosmetic {

    private String name;
    private String permission;
    private CageType type;
    private ItemStack icon;
    private JSONArray smallCage;
    private JSONArray bigCage;
    private List<JSONArray> smallFrames;
    private List<JSONArray> bigFrames;
    private long refresh;
    private boolean canBeFoundInBox;
    private static Map<UUID, BukkitTask> tasks = new HashMap<>();

    public SkyWarsCage(int id,
                       CosmeticRarity rarity,
                       String name,
                       String permission,
                       ItemStack icon,
                       boolean canBeFoundInBox,
                       CageType type,
                       JSONArray smallCage,
                       JSONArray bigCage,
                       List<JSONArray> smallFrames,
                       List<JSONArray> bigFrames,
                       long refreshInterval) {
        super(id, VisualCosmeticType.CAGE, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.smallCage = smallCage;
        this.bigCage = bigCage;
        this.smallFrames = smallFrames;
        this.bigFrames = bigFrames;
        this.refresh = refreshInterval;
        this.type = type;
        this.canBeFoundInBox = canBeFoundInBox;
    }

    public void apply(Player p, Location location) {
        apply(p, location, false);
    }

    public void apply(Player p, Location location, boolean isBig) {
        if (type == CageType.STATIC) {
            applyStatic(location, isBig);
        } else {
            applyAnimated(p, location, isBig);
        }
    }

    public void applyStatic(Location location, boolean isBig) {
        JSONArray array;
        if (!isBig) {
            array = smallCage;
        } else {
            array = bigCage;
        }

        if (array.isEmpty()) {
            SkyWarsCage.defaultCage(location, isBig);
        }
        for (Object object : array) {
            if (object instanceof String) {
                String offset = (String) object;
                double offsetX = Double.parseDouble(offset.split("; ")[0]);
                double offsetY = Double.parseDouble(offset.split("; ")[1]);
                double offsetZ = Double.parseDouble(offset.split("; ")[2]);
                Material blockMaterial = Material.matchMaterial(offset.split("; ")[3]);
                byte data = Byte.parseByte(offset.split("; ")[4]);

                Block block = location.clone().add(offsetX, offsetY, offsetZ).getBlock();
                block.setType(blockMaterial);
                BlockState state = block.getState();
                state.getData().setData(data);
                state.update(true);
            }
        }
    }

    public void applyAnimated(Player p, Location location, boolean isBig) {
        List<JSONArray> frames;
        if (!isBig) {
            frames = smallFrames;
        } else {
            frames = bigFrames;
        }

        if (frames.isEmpty()) {
            SkyWarsCage.defaultCage(location, isBig);
            return;
        }

        BukkitTask task = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                JSONArray array = frames.get(i % frames.size());
                i = (i + 1) % frames.size();
                if (array.isEmpty()) {
                    SkyWarsCage.defaultCage(location, isBig);
                    return;
                }
                if (Database.getInstance().getAccount(p.getUniqueId()) == null || Database.getInstance().getAccount(p.getUniqueId()).getArena() == null) {
                    this.cancel();
                    return;
                }
                for (Object object : array) {
                    if (object instanceof String) {
                        String offset = (String) object;
                        double offsetX = Double.parseDouble(offset.split("; ")[0]);
                        double offsetY = Double.parseDouble(offset.split("; ")[1]);
                        double offsetZ = Double.parseDouble(offset.split("; ")[2]);
                        Material blockMaterial = Material.matchMaterial(offset.split("; ")[3]);
                        byte data = Byte.parseByte(offset.split("; ")[4]);

                        Block block = location.clone().add(offsetX, offsetY, offsetZ).getBlock();
                        block.setType(blockMaterial);
                        BlockState state = block.getState();
                        state.getData().setData(data);
                        state.update(true);
                    }
                }
            }
        }.runTaskTimer(SkyWars.getInstance(), 0L, refresh);
        if (tasks.get(p.getUniqueId()) != null) {
            tasks.get(p.getUniqueId()).cancel();
            tasks.remove(p.getUniqueId());
        }
        tasks.put(p.getUniqueId(), task);
    }

    @Override
    public void preview(Player player, Object... objects) {
        boolean isBig = (boolean) objects[0];
        if (type == CageType.STATIC) {
            previewStatic(player, isBig);
        } else {
            previewAnimated(player, isBig);
        }
    }

    public void previewStatic(Player p, boolean isBig) {
        JSONArray array;
        Location cageLoc;
        if (!isBig) {
            cageLoc = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.cages.small")).getBlock().getLocation();
            array = smallCage;
        } else {
            cageLoc = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.cages.big")).getBlock().getLocation().add(0, 1, 0);
            array = bigCage;
        }

        if (array.isEmpty()) {
            SkyWarsCage.previewDefaultCage(p, cageLoc, isBig);
        }
        for (Object object : array) {
            if (object instanceof String) {
                String offset = (String) object;
                double offsetX = Double.parseDouble(offset.split("; ")[0]);
                double offsetY = Double.parseDouble(offset.split("; ")[1]);
                double offsetZ = Double.parseDouble(offset.split("; ")[2]);
                Material blockMaterial = Material.matchMaterial(offset.split("; ")[3]);
                XMaterial xMaterial = XMaterial.matchXMaterial(blockMaterial);
                MaterialData matdata = xMaterial.parseItem().getData();

                byte data = Byte.parseByte(offset.split("; ")[4]);
                matdata.setData(data);
                Location loc = cageLoc.clone().add(offsetX, offsetY, offsetZ);
                int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) loc.getX(),
                                (int) loc.getY(),
                                (int) loc.getZ()), id);

                PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, packet);
            }
        }

        sessionUUID.get(p.getUniqueId()).addEndConsumers((player) -> {
            SkyWarsCage.removePreviewCage(player.getUniqueId(), cageLoc, isBig);
        });
    }

    public void previewAnimated(Player p, boolean isBig) {
        List<JSONArray> frames;
        Location cageLoc;

        if (!isBig) {
            cageLoc = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.cages.small")).getBlock().getLocation();
            frames = smallFrames;
        } else {
            cageLoc = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.cages.big")).getBlock().getLocation().add(0, 1, 0);
            frames = bigFrames;
        }

        if (frames.isEmpty()) {
            SkyWarsCage.previewDefaultCage(p, cageLoc, isBig);
            sessionUUID.get(p.getUniqueId()).addEndConsumers((player) -> {
                SkyWarsCage.removePreviewCage(player.getUniqueId(), cageLoc, isBig);
            });
            return;
        }

        BukkitTask task = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                JSONArray array = frames.get(i % frames.size());
                i = (i + 1) % frames.size();
                if (array.isEmpty()) {
                    SkyWarsCage.previewDefaultCage(p, cageLoc, isBig);
                    return;
                }
                for (Object object : array) {
                    if (object instanceof String) {
                        String offset = (String) object;
                        double offsetX = Double.parseDouble(offset.split("; ")[0]);
                        double offsetY = Double.parseDouble(offset.split("; ")[1]);
                        double offsetZ = Double.parseDouble(offset.split("; ")[2]);
                        Material blockMaterial = Material.matchMaterial(offset.split("; ")[3]);
                        XMaterial xMaterial = XMaterial.matchXMaterial(blockMaterial);
                        MaterialData matdata = xMaterial.parseItem().getData();

                        byte data = Byte.parseByte(offset.split("; ")[4]);
                        matdata.setData(data);
                        Location loc = cageLoc.clone().add(offsetX, offsetY, offsetZ);
                        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();
                        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                                new Vector3i((int) loc.getX(),
                                        (int) loc.getY(),
                                        (int) loc.getZ()), id);

                        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, packet);
                    }
                }
            }
        }.runTaskTimer(SkyWars.getInstance(), 0L, refresh);
        if (tasks.get(p.getUniqueId()) != null) {
            tasks.get(p.getUniqueId()).cancel();
            tasks.remove(p.getUniqueId());
        }
        tasks.put(p.getUniqueId(), task);

        sessionUUID.get(p.getUniqueId()).addEndConsumers((player) -> {
            SkyWarsCage.removePreviewCage(player.getUniqueId(), cageLoc, isBig);
        });
    }

    public boolean canBeSold() {
        return false;
    }

    @Override
    public boolean canBeFoundInBox(Player player) {
        if (this.has(Database.getInstance().getAccount(player.getUniqueId())))
            return false;
        return canBeFoundInBox;
    }

    public boolean isPermissible() {
        return !this.permission.isEmpty() && !this.permission.equals("none");
    }

    public boolean hasByPermission(Player player) {
        return !isPermissible() || player.hasPermission(this.permission);
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$cage + name;
    }

    public String getRawName() {
        return name;
    }

    @Override
    public int getCoins() {
        return 0;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("a");
    }

    public ItemStack getIcon(String colorDisplay, String... lores) {
        ItemStack cloned = icon.clone();
        ItemMeta meta = cloned.getItemMeta();
        meta.setDisplayName(colorDisplay + meta.getDisplayName());
        meta.setLore(Arrays.asList(lores));
        cloned.setItemMeta(meta);
        return cloned;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Cages");
    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("cages");

    public static void setupCages() {
        CONFIG.reload();
        for (String key : CONFIG.getSection("cages").getKeys(false)) {
            ConfigurationSection section = CONFIG.getSection("cages." + key);
            LOGGER.log(Level.INFO, "Loading " + key + " cage...");
            int id = section.getInt("id");
            String name = section.getString("name");
            boolean canBeFoundInBox = section.getBoolean("canBeFoundInBox", true);
            CosmeticRarity rarity = CosmeticRarity.fromName(section.getString("rarity"));
            String permission = section.getString("permission");
            String icon = section.getString("icon");
            CageType type = CageType.valueOf(section.getString("type", "STATIC"));
            long refreshInterval = section.getLong("refresh_interval", 0L);
            JSONArray smallCage;
            JSONArray bigCage;
            List<JSONArray> smallFrames;
            List<JSONArray> bigFrames;

            if (type == CageType.STATIC) {
                try {
                    if (section.contains("small.data")) {
                        smallCage = (JSONArray) new JSONParser().parse(section.getString("small.data"));
                    } else {
                        smallCage = new JSONArray();
                        LOGGER.log(Level.WARNING, "Small cage data for \"" + key + "\" not found, leave it empty!");
                    }
                    if (section.contains("big.data")) {
                        bigCage = (JSONArray) new JSONParser().parse(section.getString("big.data"));
                    } else {
                        bigCage = new JSONArray();
                        LOGGER.log(Level.WARNING, "Big cage data for \"" + key + "\" not found, leave it empty!");
                    }
                } catch (ParseException ex) {
                    LOGGER.log(Level.WARNING, "Invalid CageData \"" + key + "\": ", ex);
                    continue;
                }

                smallFrames = Collections.emptyList();
                bigFrames = Collections.emptyList();
            } else {
                try {
                    smallFrames = new ArrayList<>();
                    if (section.contains("small.frames")) {
                        for (String jsonData : section.getStringList("small.frames")) {
                            smallFrames.add((JSONArray) new JSONParser().parse(jsonData));
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Small cage data for \"" + key + "\" not found, leave it empty!");
                    }

                    bigFrames = new ArrayList<>();
                    if (section.contains("big.frames")) {
                        for (String jsonData : section.getStringList("big.frames")) {
                            bigFrames.add((JSONArray) new JSONParser().parse(jsonData));
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Big cage data for \"" + key + "\" not found, leave it empty!");
                    }
                } catch (ParseException ex) {
                    LOGGER.log(Level.WARNING, "Invalid CageData \"" + key + "\": ", ex);
                    continue;
                }

                smallCage = new JSONArray();
                bigCage = new JSONArray();
            }

            VisualCosmetic.register(new SkyWarsCage(id, rarity, name, permission, BukkitUtils.fullyDeserializeItemStack(icon), canBeFoundInBox, type, smallCage, bigCage, smallFrames, bigFrames, refreshInterval));
        }
    }

    public static void createNew(CageSetupSession setupSession) {
        int id = 1;
        String name = setupSession.getName();
        String key = setupSession.getKey();
        CageType type = setupSession.getCageType();
        JSONArray smallCage;
        JSONArray bigCage;
        List<JSONArray> smallFrames;
        List<JSONArray> bigFrames;
        boolean canBeFoundInBox = true;
        String permission = setupSession.getPermission();
        CosmeticRarity rarity = setupSession.getRarity();
        ItemStack icon = setupSession.getIcon();
        long refreshInterval = setupSession.getRefreshInterval();

        if (type == CageType.STATIC) {
            smallCage = !setupSession.getSmallFrames().isEmpty() ? setupSession.getSmallFrames().get(0) : new JSONArray();
            bigCage = !setupSession.getBigFrames().isEmpty() ? setupSession.getBigFrames().get(0) : new JSONArray();
            smallFrames = new ArrayList<>();
            bigFrames = new ArrayList<>();
        } else {
            smallCage = new JSONArray();
            bigCage = new JSONArray();
            smallFrames = setupSession.getSmallFrames();
            bigFrames = setupSession.getBigFrames();
        }
        CONFIG.createSection("cages." + key);
        ConfigurationSection sec = CONFIG.getSection("cages." + key);
        if (!setupSession.isExisted()) {
            VisualCosmetic c = VisualCosmetic.listByType(VisualCosmeticType.CAGE).stream().filter(cosmetic -> cosmetic.getId() == 1).findAny().orElse(null);
            while (c != null) {
                id++;
                int copyId = id;
                c = VisualCosmetic.listByType(VisualCosmeticType.CAGE).stream().filter(cosmetic -> cosmetic.getId() == copyId).findAny().orElse(null);
            }
            sec.set("id", id);
        }
        sec.set("name", name);
        sec.set("rarity", rarity.name());
        sec.set("type", type.name());
        sec.set("refresh_interval", refreshInterval);
        sec.set("canBeFoundInBox", true);
        sec.set("permission", permission);
        sec.set("icon", BukkitUtils.fullySerializeItemStack(icon));
        if (type == CageType.STATIC) {
            sec.set("small.data", smallCage.toString());
            sec.set("big.data", bigCage.toString());
        } else {
            sec.set("small.frames", smallFrames.stream().map(JSONArray::toString).collect(Collectors.toList()));
            sec.set("big.frames", bigFrames.stream().map(JSONArray::toString).collect(Collectors.toList()));
        }
        CONFIG.save();
        if (!setupSession.isExisted())
            VisualCosmetic.register(
                    new SkyWarsCage(id, rarity, name, permission, icon, canBeFoundInBox, type, smallCage, bigCage, smallFrames, bigFrames, refreshInterval));
    }

    @SuppressWarnings("unchecked")
    public static JSONArray createFrame(Location location, boolean isBig) {
        JSONArray cageData = new JSONArray();
        int offset = isBig ? 2 : 1;
        location = location.getBlock().getLocation().clone().add(0.5, -1, 0.5);
        for (double y = 0; y <= 4; y++) {
            for (double x = -offset; x <= offset; x++) {
                for (double z = -offset; z <= offset; z++) {
                    if (y > 0 && y < 4) {
                        if (x == 0 && z == 0) {
                            continue;
                        }
                    }

                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() != Material.AIR) {
                        cageData.add(x + "; " + y + "; " + z + "; " + block.getType().name() + "; " + block.getData());
                    }
                }
            }
        }

        return cageData;
    }

    public static void defaultCage(Location location, boolean big) {
        location = location.clone();
        if (big) {
            location.add(0.0D, -1.0D, 0.0D);
            Location[] downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                down.getBlock().setType(Material.GLASS);
            }

            for (int i = 1; i < 4; ++i) {
                location.add(0.0D, 1.0D, 0.0D);
                Location[] uppers = new Location[]{location.clone().add(2.0D, 0.0D, 0.0D), location.clone().add(-2.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 2.0D), location.clone().add(0.0D, 0.0D, -2.0D), location.clone().add(2.0D, 0.0D, 1.0D), location.clone().add(2.0D, 0.0D, -1.0D), location.clone().add(-2.0D, 0.0D, 1.0D), location.clone().add(-2.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 2.0D), location.clone().add(-1.0D, 0.0D, -2.0D), location.clone().add(1.0D, 0.0D, -2.0D), location.clone().add(-1.0D, 0.0D, 2.0D)};
                for (Location upper : uppers) {
                    upper.getBlock().setType(Material.GLASS);
                }
            }

            location.add(0.0D, 1.0D, 0.0D);
            downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                down.getBlock().setType(Material.GLASS);
            }
        } else {
            for (double y = 0.0D; y <= 4.0D; ++y) {
                for (double x = -1.0D; x <= 1.0D; ++x) {
                    for (double z = -1.0D; z <= 1.0D; ++z) {
                        if (y <= 0.0D || y >= 4.0D || x != 0.0D || z != 0.0D) {
                            location.clone().add(x, y, z).getBlock().setType(Material.GLASS);
                        }
                    }
                }
            }
        }
    }

    public static void previewDefaultCage(Player player, Location location, boolean big) {
        location = location.clone();
        XMaterial xMaterial = XMaterial.GLASS;
        MaterialData matdata = xMaterial.parseItem().getData();
        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

        if (big) {
            location.add(0.0D, -1.0D, 0.0D);
            Location[] downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) down.getX(), (int) down.getY(), (int) down.getZ()), id);
                PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);
            }

            for (int i = 1; i < 4; ++i) {
                location.add(0.0D, 1.0D, 0.0D);
                Location[] uppers = new Location[]{location.clone().add(2.0D, 0.0D, 0.0D), location.clone().add(-2.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 2.0D), location.clone().add(0.0D, 0.0D, -2.0D), location.clone().add(2.0D, 0.0D, 1.0D), location.clone().add(2.0D, 0.0D, -1.0D), location.clone().add(-2.0D, 0.0D, 1.0D), location.clone().add(-2.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 2.0D), location.clone().add(-1.0D, 0.0D, -2.0D), location.clone().add(1.0D, 0.0D, -2.0D), location.clone().add(-1.0D, 0.0D, 2.0D)};
                for (Location upper : uppers) {
                    WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                            new Vector3i((int) upper.getX(), (int) upper.getY(), (int) upper.getZ()), id);
                    PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);
                }
            }

            location.add(0.0D, 1.0D, 0.0D);
            downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) down.getX(), (int) down.getY(), (int) down.getZ()), id);
                PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);
            }
        } else {
            for (double y = 0.0D; y <= 4.0D; ++y) {
                for (double x = -1.0D; x <= 1.0D; ++x) {
                    for (double z = -1.0D; z <= 1.0D; ++z) {
                        if (y <= 0.0D || y >= 3.0D || x != 0.0D || z != 0.0D) {
                            Location loc = location.clone().add(x, y, z);
                            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                                    new Vector3i((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()), id);
                            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);
                        }
                    }
                }
            }
        }
    }

    public static void removePreviewCage(UUID owner, Location location, boolean big) {
        Player p = Bukkit.getPlayer(owner);
        XMaterial xMaterial = XMaterial.AIR;
        MaterialData matdata = xMaterial.parseItem().getData();
        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

        if (tasks.get(owner) != null) {
            tasks.get(owner).cancel();
            tasks.remove(owner);
        }
        if (big) {
            for (double y = -1.0D; y <= 4.0D; ++y) {
                for (double x = -3.0D; x <= 3.0D; ++x) {
                    for (double z = -3.0D; z <= 3.0D; ++z) {
                        if (y <= 0.0D || y >= 3.0D || x != 0.0D || z != 0.0D) {
                            Location loc = location.clone().add(x, y, z);
                            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                                    new Vector3i((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()), id);
                            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, packet);
                        }
                    }
                }
            }
        } else {
            for (double y = 0.0D; y <= 4.0D; ++y) {
                for (double x = -1.0D; x <= 1.0D; ++x) {
                    for (double z = -1.0D; z <= 1.0D; ++z) {
                        if (y <= 0.0D || y >= 4.0D || x != 0.0D || z != 0.0D) {
                            Location loc = location.clone().add(x, y, z);
                            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                                    new Vector3i((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()), id);
                            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, packet);
                        }
                    }
                }
            }
        }
    }

    public static void remove(UUID owner, Location location, boolean big) {
        if (tasks.get(owner) != null) {
            tasks.get(owner).cancel();
            tasks.remove(owner);
        }
        if (SkyWars.we) {
            int radius;
            int height;

            if (big) {
                radius = 2;
                height = 5;
                location = location.clone().add(0.0D, -1.0D, 0.0D);
            } else {
                radius = 1;
                height = 4;
            }

            WorldEditHook.getHelper().removeRegion(location, radius, height);
            return;
        }
        if (big) {
            location.add(0.0D, -1.0D, 0.0D);
            Location[] downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                down.getBlock().setType(Material.AIR);
            }

            for (int i = 1; i < 4; ++i) {
                location.add(0.0D, 1.0D, 0.0D);
                Location[] uppers = new Location[]{location.clone().add(2.0D, 0.0D, 0.0D), location.clone().add(-2.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 2.0D), location.clone().add(0.0D, 0.0D, -2.0D), location.clone().add(2.0D, 0.0D, 1.0D), location.clone().add(2.0D, 0.0D, -1.0D), location.clone().add(-2.0D, 0.0D, 1.0D), location.clone().add(-2.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 2.0D), location.clone().add(-1.0D, 0.0D, -2.0D), location.clone().add(1.0D, 0.0D, -2.0D), location.clone().add(-1.0D, 0.0D, 2.0D)};
                for (Location upper : uppers) {
                    upper.getBlock().setType(Material.AIR);
                }
            }

            location.add(0.0D, 1.0D, 0.0D);
            downs = new Location[]{location, location.clone().add(1.0D, 0.0D, 0.0D), location.clone().add(-1.0D, 0.0D, 0.0D), location.clone().add(0.0D, 0.0D, 1.0D), location.clone().add(0.0D, 0.0D, -1.0D), location.clone().add(1.0D, 0.0D, 1.0D), location.clone().add(-1.0D, 0.0D, 1.0D), location.clone().add(1.0D, 0.0D, -1.0D), location.clone().add(-1.0D, 0.0D, -1.0D)};
            for (Location down : downs) {
                down.getBlock().setType(Material.AIR);
            }
        } else {
            for (double y = 0.0D; y <= 4.0D; ++y) {
                for (double x = -1.0D; x <= 1.0D; ++x) {
                    for (double z = -1.0D; z <= 1.0D; ++z) {
                        if (y <= 0.0D || y >= 4.0D || x != 0.0D || z != 0.0D) {
                            location.clone().add(x, y, z).getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    public enum CageType {
        STATIC,
        ANIMATED
    }
}
