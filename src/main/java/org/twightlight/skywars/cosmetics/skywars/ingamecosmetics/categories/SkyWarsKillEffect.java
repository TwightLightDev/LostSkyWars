package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.*;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects.*;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.hook.CitizensHook;
import org.twightlight.skywars.hook.PacketEventsHook;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.utils.ItemBuilder;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class SkyWarsKillEffect extends PreviewableCosmetic {

    private String name;
    private String permission;
    private ItemStack icon;
    private int coins;
    private boolean buyable;
    private boolean canBeFoundInBox;

    private static Map<NPC, CompletableFuture<Boolean>> npcSkyWarsKillEffectMap = new HashMap<>();

    public SkyWarsKillEffect(int id, String name, CosmeticRarity rarity, boolean buyable, String permission, ItemStack icon, int coins) {
        this(id, name, rarity, buyable, true, permission, icon, coins);
    }

    public SkyWarsKillEffect(int id, String name, CosmeticRarity rarity, boolean buyable, boolean canBeFoundInBox, String permission, ItemStack icon, int coins) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLEFFECT, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.buyable = buyable;
        this.canBeFoundInBox = canBeFoundInBox;
    }

    @Override
    public final void preview(Player player, Object... objects) {

        Location klocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.killeffects.attacker"));
        Location vlocation = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.killeffects.victim"));
        vlocation.setY(klocation.getY());
        klocation.getChunk().load();
        vlocation.getChunk().load();

        Map<Block, Material> affectedBlocks = new HashMap<>();

        World world = klocation.getWorld();

        int minX = Math.min(klocation.getBlockX(), vlocation.getBlockX());
        int maxX = Math.max(klocation.getBlockX(), vlocation.getBlockX());
        int minZ = Math.min(klocation.getBlockZ(), vlocation.getBlockZ());
        int maxZ = Math.max(klocation.getBlockZ(), vlocation.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                affectedBlocks.put(world.getBlockAt(x, klocation.getBlockY()-1, z), world.getBlockAt(x, klocation.getBlockY()-1, z).getType());
                world.getBlockAt(x, klocation.getBlockY()-1, z).setType(Material.BARRIER);

            }
        }

        NPC killerNPC = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "KillEffectsPreviewKillerNPC");
        killerNPC.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        killerNPC.setFlyable(true);

        killerNPC.addTrait(Equipment.class);
        killerNPC.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
        killerNPC.setName("KillEffectsPreviewKillerNPC");
        killerNPC.spawn(klocation);

        NPC victimNPC = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "KillEffectsPreviewVictimNPC");
        victimNPC.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        victimNPC.setFlyable(true);
        victimNPC.setName("KillEffectsPreviewVictimNPC");
        victimNPC.spawn(vlocation);

        Vector vector = vlocation.toVector().subtract(klocation.toVector()).normalize();

        Location navigationLocation = vlocation.clone().subtract(vector.multiply(0.5));

        killerNPC.getNavigator().setTarget(navigationLocation);

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        npcSkyWarsKillEffectMap.put(killerNPC, completableFuture);

        completableFuture.thenApply((b) -> {

            WrapperPlayServerEntityAnimation action = new WrapperPlayServerEntityAnimation(killerNPC.getEntity().getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
            WrapperPlayServerEntityStatus action1 = new WrapperPlayServerEntityStatus(victimNPC.getEntity().getEntityId(), 3);

            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, action);
            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, action1);

            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                victimNPC.destroy();

                killEffectPreview(player, vlocation);
            }, 5L);
            return b;
        });

        sessionUUID.get(player.getUniqueId()).addEndConsumers((p) -> {
            npcSkyWarsKillEffectMap.remove(killerNPC);
            killerNPC.destroy();

            for (Block block : affectedBlocks.keySet()) {

                block.setType(affectedBlocks.get(block));

            }
        });
    }

    public abstract void killEffectPreview(Player player, Location location);

    public static class NavigationCheck implements Listener {
        @EventHandler
        public void onNavigationComplete(NavigationCompleteEvent e) {
            NPC npc = e.getNPC();

            if (npc.getName().equals("KillEffectsPreviewKillerNPC") && npcSkyWarsKillEffectMap.containsKey(npc)) {
                npcSkyWarsKillEffectMap.get(npc).complete(true);
            }

        }
    }

    public abstract void execute(Player killer, Player victim, Location location);

    public boolean canBeSold() {
        return buyable;
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
    public boolean has(Account account) {
        if (isPermissible()) {
            return this.has(account, this.getMode()) || this.hasByPermission(account.getPlayer());
        }
        return this.has(account, this.getMode());
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$killeffect + this.name;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Kill Effects");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public static void setupKillEffects() {
        CONFIG.reload();
        checkIfAbsent("lightning-strike");
        CosmeticServer.SKYWARS.addCosmetic(new LightningStrikeEffect());
        checkIfAbsent("batcrux");
        CosmeticServer.SKYWARS.addCosmetic(new BatCruxEffect());
        checkIfAbsent("burning-shoes");
        CosmeticServer.SKYWARS.addCosmetic(new BurningShoesEffect());
        checkIfAbsent("firework");
        CosmeticServer.SKYWARS.addCosmetic(new FireworkEffect());
        checkIfAbsent("heart-aura");
        CosmeticServer.SKYWARS.addCosmetic(new HeartAuraEffect());
        checkIfAbsent("rekt");
        CosmeticServer.SKYWARS.addCosmetic(new RektEffect());
        checkIfAbsent("squid-missile");
        CosmeticServer.SKYWARS.addCosmetic(new SquidMissileEffect());
        checkIfAbsent("tornado");
        CosmeticServer.SKYWARS.addCosmetic(new TornadoEffect());
        checkIfAbsent("crying");
        CosmeticServer.SKYWARS.addCosmetic(new CryingEffect());
        checkIfAbsent("explosion");
        CosmeticServer.SKYWARS.addCosmetic(new ExplosionEffect());
        checkIfAbsent("lucky-block");
        CosmeticServer.SKYWARS.addCosmetic(new LuckyBlockEffect());
        checkIfAbsent("rainbow");
        CosmeticServer.SKYWARS.addCosmetic(new RainbowEffect());
        checkIfAbsent("rebirth");
        CosmeticServer.SKYWARS.addCosmetic(new RebirthEffect());
        checkIfAbsent("sadface");
        CosmeticServer.SKYWARS.addCosmetic(new SadFaceBannerEffect());
        checkIfAbsent("shatter");
        CosmeticServer.SKYWARS.addCosmetic(new ShatterEffect());
        checkIfAbsent("volcano");
        CosmeticServer.SKYWARS.addCosmetic(new VolcanoEffect());
        checkIfAbsent("mysterybox");
        CosmeticServer.SKYWARS.addCosmetic(new MysteryBoxEffect());
    }


    private static void checkIfAbsent(String key) {
        if (CONFIG.contains(key)) {
            return;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(SkyWars.getInstance().getResource("killeffects.yml"), "UTF-8"));
            for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
                CONFIG.set(key + "." + dataKey, config.get(key + "." + dataKey));
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
