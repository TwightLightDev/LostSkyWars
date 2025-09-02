package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.*;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.SpiralFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SkyWarsTrail extends PreviewableCosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private ItemStack icon;
    private Consumer<ProjectileLaunchEvent> consumer;

    public SkyWarsTrail(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity,
                        boolean buyable, int coins, Consumer<ProjectileLaunchEvent> consumer) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_TRAIL, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.icon = icon;
        this.consumer = consumer;
    }
    @Override
    public void preview(Player player) {

    }

    public boolean canBeSold() {
        return buyable;
    }

    public Consumer<ProjectileLaunchEvent> getConsumer() {
        return consumer;
    }

    @Override
    public boolean canBeFoundInBox(Player player) {
        return !isPermissible() || !this.hasByPermission(player);
    }

    public boolean isPermissible() {
        return !this.permission.isEmpty() && !this.permission.equals("none");
    }

    public boolean hasByPermission(Player player) {
        return !isPermissible() || player.hasPermission(this.permission);
    }

    @Override
    public boolean has(Account account) {
        if (isPermissible()) {
            return this.has(account, this.getMode()) || this.hasByPermission(account.getPlayer());
        }
        return this.has(account, this.getMode());
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$trail + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("§a");
    }

    public ItemStack getIcon(String colorDisplay, String... lores) {
        ItemStack cloned = this.icon.clone();
        ItemMeta meta = cloned.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        meta.setDisplayName(colorDisplay + meta.getDisplayName());
        List<String> list = new ArrayList<>();
        if (meta.getLore() != null) {
            list.addAll(meta.getLore());
        }
        list.addAll(Arrays.asList(lores));
        meta.setLore(list);
        cloned.setItemMeta(meta);
        return cloned;
    }

    public int getCoins() {
        return coins;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("ProjectileTrails");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("projectiletrails");

    public static void setupProjectileTrails() {
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            String permission = sec.getString("permission");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            int price = sec.getInt("price");
            TrailShape shape = TrailShape.fromString(sec.getString("shape"));
            TrailType type = TrailType.fromString(sec.getString("type"));
            int count = sec.getInt("count", 1);

            ParticleType particle = null;
            ItemStack item = null;
            if (type == TrailType.PARTICLE) {
                particle = ParticleType.of(sec.getString("particle"));
            } else if (type == TrailType.DROPPED_ITEM) {
                item = BukkitUtils.fullyDeserializeItemStack(sec.getString("material"));
            }

            ParticleType finalParticle = particle;
            ItemStack finalItem = item;

            Consumer<ProjectileLaunchEvent> consumer = e -> {
                if (e.getEntity().getShooter() instanceof Player) {
                    Player p = (Player) e.getEntity().getShooter();
                    if (shape == TrailShape.SPIRAL) {
                        int rotationSpeed = sec.getInt("options.rotation_speed", 30);
                        double radius = sec.getDouble("options.radius", .3);
                        double spacing = sec.getDouble("options.spacing", .1);

                        if (type == TrailType.PARTICLE) {
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) ->
                                            finalParticle.spawn(e.getEntity().getWorld(), loc, count, 0, 0, 0, 0), e.getEntity(), p
                            , 0, rotationSpeed, radius, spacing, false);
                        } else if (type == TrailType.DROPPED_ITEM) {
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) -> {
                                World world = loc.getWorld();
                                if (world != null) {
                                    Item dropped = world.dropItem(loc, finalItem);
                                    dropped.setVelocity(new Vector(0, 0, 0));

                                    dropped.setPickupDelay(Integer.MAX_VALUE);

                                    Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), dropped::remove, 100L);
                                }
                            }, e.getEntity(), p
                                    , 0, rotationSpeed, radius, spacing, false);
                        }
                    } else if (shape == TrailShape.DOUBLE_SPIRAL) {
                        int rotationSpeed = sec.getInt("options.rotation_speed", 30);
                        double radius = sec.getDouble("options.radius", .3);
                        double spacing = sec.getDouble("options.spacing", .1);

                        if (type == TrailType.PARTICLE) {
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) ->
                                            finalParticle.spawn(e.getEntity().getWorld(), loc, count, 0, 0, 0, 0), e.getEntity(), p
                                    , 0, rotationSpeed, radius, spacing, false);
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) ->
                                            finalParticle.spawn(e.getEntity().getWorld(), loc, count, 0, 0, 0, 0), e.getEntity(), p
                                    , 0, rotationSpeed, radius, spacing, true);
                        } else if (type == TrailType.DROPPED_ITEM) {
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) -> {
                                        World world = loc.getWorld();
                                        if (world != null) {
                                            Item dropped = world.dropItem(loc, finalItem);
                                            dropped.setVelocity(new Vector(0, 0, 0));

                                            dropped.setPickupDelay(Integer.MAX_VALUE);

                                            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), dropped::remove, 100L);
                                        }
                                    }, e.getEntity(), p
                                    , 0, rotationSpeed, radius, spacing, false);
                            SpiralFactory.createNewSpiral(SkyWars.getInstance(), (loc, shooter) -> {
                                        World world = loc.getWorld();
                                        if (world != null) {
                                            Item dropped = world.dropItem(loc, finalItem);
                                            dropped.setVelocity(new Vector(0, 0, 0));

                                            dropped.setPickupDelay(Integer.MAX_VALUE);

                                            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), dropped::remove, 100L);
                                        }
                                    }, e.getEntity(), p
                                    , 0, rotationSpeed, radius, spacing, true);
                        }
                    } else if (shape == TrailShape.LINE) {
                        if (type == TrailType.PARTICLE) {
                            new BukkitRunnable() {
                                final Projectile proj = e.getEntity();

                                @Override
                                public void run() {
                                    if (proj.isDead() || proj.isOnGround()) {
                                        this.cancel();
                                        return;
                                    }
                                    finalParticle.spawn(e.getEntity().getWorld(), e.getEntity().getLocation(), 1, 0, 0, 0, 0);
                                }
                            }.runTaskTimer(SkyWars.getInstance(), 2, 0L);

                        } else if (type == TrailType.DROPPED_ITEM) {
                            new BukkitRunnable() {
                                final Projectile proj = e.getEntity();

                                @Override
                                public void run() {
                                    if (proj.isDead() || proj.isOnGround()) {
                                        this.cancel();
                                        return;
                                    }
                                    World world = proj.getWorld();
                                    if (world != null) {
                                        Item dropped = world.dropItem(proj.getLocation(), finalItem);
                                        dropped.setVelocity(new Vector(0, 0, 0));

                                        dropped.setPickupDelay(Integer.MAX_VALUE);

                                        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), dropped::remove, 100L);
                                    }
                                }
                            }.runTaskTimer(SkyWars.getInstance(), 2, 0L);
                        }
                    }
                }
            };

            SkyWarsTrail trail = new SkyWarsTrail(id, name, permission, icon, rarity, buyable, price, consumer);

            CosmeticServer.SKYWARS.addCosmetic(trail);
        }
    }

    public enum TrailShape {
        SPIRAL,
        DOUBLE_SPIRAL,
        LINE;

        public static TrailShape fromString(String string) {
            try {
                return TrailShape.valueOf(string.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return TrailShape.LINE;
            }
        }
    }

    public enum TrailType {
        DROPPED_ITEM,
        PARTICLE;

        public static TrailType fromString(String string) {
            try {
                return TrailType.valueOf(string.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return TrailType.PARTICLE;
            }
        }
    }
}

