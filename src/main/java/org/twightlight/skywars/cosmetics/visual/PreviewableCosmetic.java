package org.twightlight.skywars.cosmetics.visual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.common.reflect.TypeToken;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.integration.packetevents.PacketEventsIntegration;
import org.twightlight.skywars.integration.protocollib.ProtocolLibIntegration;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Filter;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Order;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;
import org.twightlight.skywars.utils.string.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class PreviewableCosmetic extends VisualCosmetic {
    protected static final YamlWrapper PREVIEWCONFIG = YamlWrapper.getConfig("cosmeticspreview");
    protected static final Map<UUID, PreviewSession> sessionUUID = new ConcurrentHashMap<>();
    protected static final Map<VisualCosmeticType, List<PreviewSession>> sessionCosmeticType = new ConcurrentHashMap<>();

    public PreviewableCosmetic(int id, VisualCosmeticType visualType, CosmeticRarity rarity) {
        super(id, visualType, rarity);
    }

    public abstract void preview(Player player, Object... objects);

    public final void playPreview(Player player, long duration, Class<?> returns, Order order, Filter filter, String searchQuery, Object... objects) {
        if (SkyWars.packetevents && SkyWars.protocollib) {
            try {
                if (sessionCosmeticType.containsKey(getVisualType())) {
                    if (getVisualType() == VisualCosmeticType.TRAIL && !sessionCosmeticType.get(getVisualType()).isEmpty()) {
                        player.sendMessage(ChatColor.RED + "&cThere is no available previewing space for you! Please wait!");
                        return;
                    } else if (getVisualType() == VisualCosmeticType.KILL_EFFECT && !sessionCosmeticType.get(getVisualType()).isEmpty()) {
                        player.sendMessage(ChatColor.RED + "&cThere is no available previewing space for you! Please wait!");
                        return;
                    }

                }
                Location playerLocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("player-location."+ getVisualType().getPreviewId()));

                XMaterial xMaterial = XMaterial.BARRIER;
                MaterialData matdata = xMaterial.parseItem().getData();

                int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) playerLocation.getX(),
                                (int) playerLocation.getY()-1,
                                (int) playerLocation.getZ()), id);

                PacketEventsIntegration.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

                new PreviewSession(this, player, playerLocation, player.getLocation(), duration, returns, order, filter, searchQuery);

                Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                    preview(player, objects);
                }, 10L);

            } catch (Exception e) {
                player.sendMessage(StringUtils.formatColors("&cPlayer's location not found or cosmetic's location is missing!"));
                e.printStackTrace();
            }
        }
    }


    public static class PreviewSession implements Listener {
        private List<Consumer<Player>> consumers;
        private PreviewableCosmetic cosmetic;
        private UUID uuid;
        private Location previewLoc;
        public PreviewSession(PreviewableCosmetic cosmetic, Player player, Location previewLoc, Location initialLocation, long duration, Class<?> returns, Order order, Filter filter, String searchQuery) {
            player.closeInventory();
            this.cosmetic = cosmetic;
            consumers = new ArrayList<>();
            sessionUUID.put(player.getUniqueId(), this);
            sessionCosmeticType.computeIfAbsent(cosmetic.getVisualType(), (k) -> new ArrayList<>()).add(this);
            uuid = player.getUniqueId();
            this.previewLoc = previewLoc;
            Bukkit.getPluginManager().registerEvents(this, SkyWars.getInstance());
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
            player.teleport(previewLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            setGameMode(player, GameMode.SPECTATOR);

            ItemStack[] itemStacks = player.getInventory().getContents();
            ItemStack[] armors = player.getInventory().getArmorContents();
            player.getInventory().clear();
            player.updateInventory();


            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                HandlerList.unregisterAll(this);
                sessionUUID.remove(player.getUniqueId());
                sessionCosmeticType.get(cosmetic.getVisualType()).remove(this);
                if (!player.isOnline()) {
                    return;
                }
                Location playerLocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("player-location." + cosmetic.getVisualType().getPreviewId()));

                XMaterial xMaterial = XMaterial.AIR;
                MaterialData matdata = xMaterial.parseItem().getData();

                int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                        new Vector3i((int) playerLocation.getX(),
                                (int) playerLocation.getY() - 1,
                                (int) playerLocation.getZ()), id);

                PacketEventsIntegration.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

                if (consumers != null) {
                    consumers.forEach((consumer) -> {
                        consumer.accept(player);
                    });
                }

                PacketContainer camera1 = ProtocolLibIntegration.getProtocolManager().createPacket(PacketType.Play.Server.CAMERA);
                camera1.getIntegers().write(0, player.getEntityId());

                try {
                    ProtocolLibIntegration.getProtocolManager().sendServerPacket(player, camera1);

                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.teleport(initialLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.setGameMode(gameMode);
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

                player.getInventory().setArmorContents(armors);
                player.getInventory().setContents(itemStacks);
                player.updateInventory();


                Constructor<?> constructor;
                try {
                    constructor = returns.getConstructor(Player.class, Order.class, Filter.class, String.class);
                    constructor.newInstance(player, order, filter, searchQuery);

                } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }, duration);
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
        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            if (e.getPlayer().getUniqueId().equals(uuid)) {
                consumers.clear();
            }
        }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (e.getPlayer().getUniqueId().equals(uuid)) {
                e.setTo(previewLoc);
            }
        }
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

    public static YamlWrapper getPreviewConfig() {
        return PREVIEWCONFIG;
    }
}
