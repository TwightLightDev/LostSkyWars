package org.twightlight.skywars.cosmetics;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.common.reflect.TypeToken;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.PacketEventsHook;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Filter;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Order;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public abstract class PreviewableCosmetic extends Cosmetic {
    protected static final ConfigUtils PREVIEWCONFIG = ConfigUtils.getConfig("cosmeticspreview");
    protected static final Map<UUID, PreviewSession> sessionUUID = new HashMap<>();
    protected static final Map<CosmeticType, List<PreviewSession>> sessionCosmeticType = new HashMap<>();

    public PreviewableCosmetic(int id, CosmeticServer server, CosmeticType type, CosmeticRarity rarity) {
        super(id, server, type, rarity);
    }

    public abstract void preview(Player player, Object... objects);

    public final void playPreview(Player player, long duration, Class<?> returns, Order order, Filter filter, String searchQuery, Object... objects) {
        if (SkyWars.packetevents) {
            try {
                if (sessionCosmeticType.containsKey(getType())) {
                    if (getType() == CosmeticType.SKYWARS_TRAIL && !sessionCosmeticType.get(getType()).isEmpty()) {
                        player.sendMessage(ChatColor.RED + "&cThere is no available previewing space for you! Please wait!");
                        return;
                    } else if (getType() == CosmeticType.SKYWARS_KILLEFFECT && !sessionCosmeticType.get(getType()).isEmpty()) {
                        player.sendMessage(ChatColor.RED + "&cThere is no available previewing space for you! Please wait!");
                        return;
                    }

                }
                Location playerLocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("player-location."+ getType().getPreviewID()));

                new PreviewSession(this, player, playerLocation, player.getLocation(), duration, returns, order, filter, searchQuery);

                XMaterial xMaterial = XMaterial.BARRIER;
                MaterialData matdata = xMaterial.parseItem().getData();

                int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) playerLocation.getX(),
                                (int) playerLocation.getY()-1,
                                (int) playerLocation.getZ()), id);

                PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

                preview(player, objects);
            } catch (Exception e) {
                player.sendMessage(StringUtils.formatColors("&cPlayer's location not found or cosmetic's location is missing!"));
                e.printStackTrace();
            }
        }
    }


    public static class PreviewSession {
        private List<Consumer<Player>> consumers;
        private PreviewableCosmetic cosmetic;

        public PreviewSession(PreviewableCosmetic cosmetic, Player player, Location previewLoc, Location initialLocation, long duration, Class<?> returns, Order order, Filter filter, String searchQuery) {
            player.closeInventory();
            this.cosmetic = cosmetic;
            consumers = new ArrayList<>();
            sessionUUID.put(player.getUniqueId(), this);
            sessionCosmeticType.computeIfAbsent(cosmetic.getType(), (k) -> new ArrayList<>()).add(this);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) duration, 2, false, false));

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (player1.equals(player))
                    continue;
                player1.hidePlayer(player);
            }
            User user = User.getFromUUID(player.getUniqueId());
            if (user != null) {
                user.setScoreboardVisibility(false, false);
            }

            previewLoc.getChunk().load(true);
            GameMode gameMode = player.getGameMode();
            int standID = summonCameraStand(player, previewLoc);
            player.teleport(previewLoc);
            player.setMetadata("frozen", new FixedMetadataValue(SkyWars.getInstance(), true));
            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                setGameMode(player, GameMode.SPECTATOR);

                WrapperPlayServerCamera playPreviewCamera = new WrapperPlayServerCamera(standID);
                PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, playPreviewCamera);
                ItemStack[] itemStacks = player.getInventory().getContents();
                ItemStack[] armors = player.getInventory().getArmorContents();
                player.getInventory().clear();
                player.updateInventory();
                player.removeMetadata("frozen", SkyWars.getInstance());
                Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                    sessionUUID.remove(player.getUniqueId());
                    sessionCosmeticType.get(cosmetic.getType()).remove(this);
                    Location playerLocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("player-location." + cosmetic.getType().getPreviewID()));

                    XMaterial xMaterial = XMaterial.AIR;
                    MaterialData matdata = xMaterial.parseItem().getData();

                    int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

                    WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                            new Vector3i((int) playerLocation.getX(),
                                    (int) playerLocation.getY() - 1,
                                    (int) playerLocation.getZ()), id);

                    PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

                    if (consumers != null) {
                        consumers.forEach((consumer) -> {
                            consumer.accept(player);
                        });
                    }

                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.teleport(initialLocation);
                    if (user != null) {
                        boolean showScoreboard = Boolean.parseBoolean(LobbySettings.getDatabase().getData(player, "showScoreboard", new TypeToken<String>() {
                        }, "true"));

                        user.setScoreboardVisibility(showScoreboard, false);
                    }
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        if (player1.equals(player))
                            continue;
                        player1.showPlayer(player);
                    }
                    sessionUUID.remove(player.getUniqueId());

                    int playerID = player.getEntityId();
                    WrapperPlayServerCamera playResetCamera = new WrapperPlayServerCamera(playerID);
                    PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, playResetCamera);
                    removeCameraStand(player, standID);
                    player.getInventory().setArmorContents(armors);
                    player.getInventory().setContents(itemStacks);
                    player.updateInventory();
                    setGameMode(player, gameMode);

                    Constructor<?> constructor;
                    try {
                        constructor = returns.getConstructor(Player.class, Order.class, Filter.class, String.class);
                        constructor.newInstance(player, order, filter, searchQuery);

                    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                             IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }


                }, duration);
            }, 2L);
        }

        @SafeVarargs
        public final void addEndConsumers(Consumer<Player>... consumers) {
            for (Iterator<Consumer<Player>> it = Arrays.stream(consumers).iterator(); it.hasNext(); ) {
                Consumer<Player> consumer = it.next();
                this.consumers.add(consumer);
            }
        }

        public PreviewableCosmetic getCosmetic() {
            return cosmetic;
        }
    }

    private static void removeCameraStand(Player player, int entityID) {



        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityID);
        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, destroyEntities);

    }

    private static int summonCameraStand(Player p, Location location) {

        UUID uuid = UUID.randomUUID();
        int entityId = SpigotReflectionUtil.generateEntityId();
        com.github.retrooper.packetevents.protocol.world.Location location1 = new com.github.retrooper.packetevents.protocol.world.Location(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        WrapperPlayServerSpawnEntity standPacket = new WrapperPlayServerSpawnEntity(entityId, uuid, EntityTypes.ARMOR_STAND, location1, location1.getYaw(), 0, null);
        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport(entityId, location1, true);

        EntityData<Byte> entityData = new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20);

        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(entityData));

        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, standPacket);
        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, entityMetadata);
        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(p, teleport);

        return entityId;
    }

    private static void setGameMode(Player p, org.bukkit.GameMode gameMode) {
        int gameModeInt = 0;
        switch (gameMode) {
            case CREATIVE:
                gameModeInt = 1;
                break;
            case ADVENTURE:
                gameModeInt = 2;
                break;
            case SPECTATOR:
                gameModeInt = 3;
                break;
        }
        WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, gameModeInt);
        PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
    }

    public static ConfigUtils getPreviewConfig() {
        return PREVIEWCONFIG;
    }
}
