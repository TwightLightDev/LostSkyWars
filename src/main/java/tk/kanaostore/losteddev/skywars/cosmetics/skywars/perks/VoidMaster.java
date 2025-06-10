package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

import java.util.concurrent.ThreadLocalRandom;

public class VoidMaster extends SkyWarsPerk {

    private int mode;
    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public VoidMaster(int mode) {
        super(CONFIG.getInt("voidmaster.id"),
                CONFIG.getString("voidmaster.name"),
                CosmeticRarity.fromName(CONFIG.getString("voidmaster.rarity")),
                CONFIG.getBoolean("voidmaster.buyable", true),
                CONFIG.getString("voidmaster.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("voidmaster.icon").replace("{percentage}", CONFIG.getInt("voidmaster.percentage") + "%")),
                CONFIG.getInt("voidmaster.price"));
        this.mode = mode;

        this.percentage = CONFIG.getInt("voidmaster.percentage");

        this.register(Main.getInstance());
    }

    @EventHandler
    public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
        if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
            Account account = Database.getInstance().getAccount(((Player) evt.getKiller()).getUniqueId());
            if (account == null) {
                return;
            }
            if (!this.selected(account)) {
                return;
            }
            if (ThreadLocalRandom.current().nextInt(100) < percentage) {
                evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("ENDER_PEARL : 1"));
            }
        }
    }

    @Override
    public int getMode() {
        return mode;
    }
}
