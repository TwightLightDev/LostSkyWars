package org.twightlight.skywars.setup.cage;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsCage;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;

import java.util.*;

public class CageSetupSession implements Listener {
    private static Map<UUID, CageSetupSession> CREATING = new HashMap<>();
    private static ItemStack smallMode = new ItemBuilder(XMaterial.GOLD_INGOT).setName("&aSmall cage mode &8(Click to toggle!)").toItemStack();
    private static ItemStack bigMode = new ItemBuilder(XMaterial.GOLD_BLOCK).setName("&aBig cage mode &8(Click to toggle!)").toItemStack();
    private static ItemStack add = new ItemBuilder(XMaterial.LIME_WOOL).setDurability(Short.parseShort("5")).setName("&aAdd a frame").toItemStack();
    private static ItemStack remove = new ItemBuilder(XMaterial.RED_WOOL).setDurability(Short.parseShort("14")).setName("&cRemove the last frame").toItemStack();
    private static YamlWrapper CONFIG = YamlWrapper.getConfig("cages");

    private String key;
    private final UUID uuid;
    private String name;
    private CosmeticRarity rarity;
    private String perm;
    private ItemStack icon;
    private SkyWarsCage.CageType cageType = SkyWarsCage.CageType.STATIC;
    private boolean setupBigFrame = false;
    private final List<JSONArray> smallFrames = new ArrayList<>();
    private final List<JSONArray> bigFrames = new ArrayList<>();
    private long refreshInterval = -1L;
    private boolean existed = false;

    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();


    public CageSetupSession(Player p, String key) {
        CREATING.put(p.getUniqueId(), this);
        this.uuid = p.getUniqueId();
        this.key = key;
        saveInventory(p);
        Bukkit.getPluginManager().registerEvents(this, SkyWars.getInstance());
        p.getInventory().setItem(0, smallMode);
        p.getInventory().setItem(1, add);
        p.getInventory().setItem(2, remove);
        if (CONFIG.contains("cages." + key)) {
            loadIfExist();
            existed = true;
        }
        p.updateInventory();
        sendProgress();
    }

    public boolean isExisted() {
        return existed;
    }

    public void end() {
        CREATING.remove(uuid);
        SkyWarsCage.createNew(this);
        restoreInventory(Bukkit.getPlayer(uuid));
        HandlerList.unregisterAll(this);
    }

    public void exit() {
        CREATING.remove(uuid);
        restoreInventory(Bukkit.getPlayer(uuid));
        HandlerList.unregisterAll(this);

    }


    public static CageSetupSession getSessionFromUUID(UUID uuid) {
        return CREATING.get(uuid);
    }

    public String getName() {
        return name;
    }

    public CosmeticRarity getRarity() {
        return rarity;
    }

    public String getPermission() {
        return perm;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<JSONArray> getBigFrames() {
        return bigFrames;
    }

    public List<JSONArray> getSmallFrames() {
        return smallFrames;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public SkyWarsCage.CageType getCageType() {
        return cageType;
    }

    public String getKey() {
        return key;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setCageType(SkyWarsCage.CageType cageType) {
        this.cageType = cageType;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public void setRarity(CosmeticRarity rarity) {
        this.rarity = rarity;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void removeLast(List<JSONArray> list) {
        if (!list.isEmpty()) {
            list.remove(list.size()-1);
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Successfully removed the last frame!");
        } else {
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "There is no frame to remove!");
        }
    }

    public void sendProgress() {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        p.sendMessage(" ");
        p.sendMessage("§dSetup - Cage §b(" + key + ")");

        // Name
        sendClickableLine(p,
                "/lsw setup cage name <name>",
                "Name",
                name != null,
                name);

        // Rarity
        sendClickableLine(p,
                "/lsw setup cage rarity <rarity>",
                "Rarity",
                rarity != null,
                rarity != null ? rarity.name() : null);

        // Permission
        sendClickableLine(p,
                "/lsw setup cage permission <permission>",
                "Permission",
                perm != null,
                perm);

        // Icon
        sendClickableLine(p,
                "/lsw setup cage icon",
                "Icon",
                icon != null,
                icon != null ? icon.getType().name() : null);

        // Cage type
        sendClickableLine(p,
                "/lsw setup cage type <STATIC/ANIMATED>",
                "Cage Type",
                cageType != null,
                cageType != null ? cageType.name() : null);

        sendClickableLine(p,
                "/lsw setup cage refresh <ticks>",
                "Refresh Interval",
                refreshInterval > -1,
                refreshInterval > -1 ? refreshInterval + " ticks" : null);

        if (cageType == SkyWarsCage.CageType.STATIC) {
            p.sendMessage("§6Small Form §f- " + (smallFrames.isEmpty() || smallFrames.get(0).isEmpty() ? "§cNot Set" : "§aSet"));
            p.sendMessage("§6Big Form §f- " + (bigFrames.isEmpty() || bigFrames.get(0).isEmpty() ? "§cNot Set" : "§aSet"));
        } else {
            p.sendMessage("§6Small Form §f- §a" + smallFrames.size() + " frame(s)");
            p.sendMessage("§6Big Form §f- §a" + bigFrames.size() + " frame(s)");
        }
        p.sendMessage(" ");

        TextComponent finish = new TextComponent("  §a§lFINISH!");
        finish.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lsw setup cage finish"));
        finish.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to finish setup!").create()));

        TextComponent space = new TextComponent("  ");

        TextComponent exit = new TextComponent("  §c§lEXIT!");
        exit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lsw setup cage exit"));
        exit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to exit setup").create()));

        finish.addExtra(space);
        finish.addExtra(exit);

        p.spigot().sendMessage(finish);

        p.sendMessage(" ");
    }

    public void saveInventory(Player player) {
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
        savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
        player.getInventory().clear();
    }

    public void restoreInventory(Player player) {
        player.getInventory().clear();
        if (savedInventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(savedInventories.get(player.getUniqueId()));
        }
        if (savedArmor.containsKey(player.getUniqueId())) {
            player.getInventory().setArmorContents(savedArmor.get(player.getUniqueId()));
        }
        player.updateInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getSlot() == 0 || e.getSlot() == 1) {
            CageSetupSession setupSession = CageSetupSession.getSessionFromUUID(e.getWhoClicked().getUniqueId());
            if (setupSession != null) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if (e.getItem().isSimilar(smallMode)) {
                e.setCancelled(true);
                e.getPlayer().getInventory().setItem(0, bigMode);
                e.getPlayer().sendMessage(ChatColor.GREEN + "Changed to Big Cage Setup!");
                e.getPlayer().updateInventory();
                setupBigFrame = true;
            } else if (e.getItem().isSimilar(bigMode)) {
                e.setCancelled(true);
                e.getPlayer().getInventory().setItem(0, smallMode);
                e.getPlayer().sendMessage(ChatColor.GREEN + "Changed to Small Cage Setup!");
                e.getPlayer().updateInventory();
                setupBigFrame = false;
            } else if (e.getItem().isSimilar(add)) {
                e.setCancelled(true);
                JSONArray array = SkyWarsCage.createFrame(e.getPlayer().getLocation(), setupBigFrame);
                if (array.isEmpty()) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You cannot add empty frame!");
                    return;
                }
                if (setupBigFrame) {
                    if (cageType == SkyWarsCage.CageType.STATIC && !bigFrames.isEmpty()) {
                        bigFrames.clear();
                    }
                    bigFrames.add(array);
                    sendProgress();
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully added a new frame for Big Cage!");
                } else {
                    if (cageType == SkyWarsCage.CageType.STATIC && !smallFrames.isEmpty()) {
                        smallFrames.clear();
                    }
                    smallFrames.add(array);
                    sendProgress();
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully added a new frame for Small Cage!");
                }
            } else if (e.getItem().isSimilar(remove)) {
                e.setCancelled(true);
                if (setupBigFrame) {
                    removeLast(bigFrames);
                    sendProgress();
                } else {
                    removeLast(smallFrames);
                    sendProgress();
                }
            }
        }
    }

    private void sendClickableLine(Player player, String command, String label, boolean set, String value) {
        TextComponent line = new TextComponent("§6" + command + " §f- §7" + label + ": ");
        TextComponent status = new TextComponent(set ? "§a(Set)" : "§c(Not Set)");

        if (set && value != null) {
            status.addExtra(" §7- §e" + value);
        }

        line.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(command).create()));

        line.addExtra(status);
        player.spigot().sendMessage(line);

    }

    @SuppressWarnings("unchecked")
    private void loadIfExist() {
        if (!CONFIG.contains("cages." + key)) return;
        org.bukkit.configuration.ConfigurationSection section = CONFIG.getSection("cages." + key);
        if (section == null) return;

        this.name = section.getString("name");
        this.rarity = CosmeticRarity.fromName(section.getString("rarity"));
        this.perm = section.getString("permission");
        this.icon = BukkitUtils.fullyDeserializeItemStack(section.getString("icon"));
        this.cageType = SkyWarsCage.CageType.valueOf(section.getString("type", "STATIC"));
        this.refreshInterval = section.getLong("refresh_interval", 0L);

        try {
            if (cageType == SkyWarsCage.CageType.STATIC) {
                if (section.contains("small.data")) {
                    JSONArray small = (JSONArray) new org.json.simple.parser.JSONParser().parse(section.getString("small.data"));
                    if (small != null) this.smallFrames.add(small);
                }
                if (section.contains("big.data")) {
                    JSONArray big = (JSONArray) new org.json.simple.parser.JSONParser().parse(section.getString("big.data"));
                    if (big != null) this.bigFrames.add(big);
                }
            } else {
                // ANIMATED cages store lists of frames
                if (section.contains("small.frames")) {
                    for (String json : section.getStringList("small.frames")) {
                        JSONArray frame = (JSONArray) new org.json.simple.parser.JSONParser().parse(json);
                        this.smallFrames.add(frame);
                    }
                }
                if (section.contains("big.frames")) {
                    for (String json : section.getStringList("big.frames")) {
                        JSONArray frame = (JSONArray) new org.json.simple.parser.JSONParser().parse(json);
                        this.bigFrames.add(frame);
                    }
                }
            }
        } catch (Exception ex) {
            SkyWars.getInstance().getLogger().warning("Failed to parse cage data for " + key + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
