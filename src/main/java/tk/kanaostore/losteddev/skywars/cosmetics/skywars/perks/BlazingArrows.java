package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

import java.util.concurrent.ThreadLocalRandom;

public class BlazingArrows extends SkyWarsPerk {

    private int mode;
    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public BlazingArrows(int mode) {
        super(CONFIG.getInt("blazingarrow.id"),
                CONFIG.getString("blazingarrow.name"),
                CosmeticRarity.fromName(CONFIG.getString("blazingarrow.rarity")),
                CONFIG.getBoolean("blazingarrow.buyable", true),
                CONFIG.getString("blazingarrow.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("blazingarrow.icon").replace("{percentage}", CONFIG.getInt("blazingarrow.percentage") + "%")),
                CONFIG.getInt("blazingarrow.price"));
        this.mode = mode;

        this.percentage = CONFIG.getInt("blazingarrow.percentage");

        this.register(Main.getInstance());
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Account account = Database.getInstance().getAccount(((Player) evt.getEntity()).getUniqueId());
            if (account == null) {
                return;
            }
            if (!this.selected(account)) {
                return;
            }
            if (isAbleToUse((Player) evt.getEntity()) && ThreadLocalRandom.current().nextInt(100) < percentage) {
                ((Player) evt.getEntity()).getInventory().addItem(new ItemStack(Material.ARROW));
            }
        }
    }

    @Override
    public int getMode() {
        return mode;
    }
}
