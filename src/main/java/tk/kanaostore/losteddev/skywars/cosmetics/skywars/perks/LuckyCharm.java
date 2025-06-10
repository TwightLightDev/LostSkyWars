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

public class LuckyCharm extends SkyWarsPerk {

    private int mode;
    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public LuckyCharm(int mode) {
        super(CONFIG.getInt("luckycharm.id"),
                CONFIG.getString("luckycharm.name"),
                CosmeticRarity.fromName(CONFIG.getString("luckycharm.rarity")),
                CONFIG.getBoolean("luckycharm.buyable", true),
                CONFIG.getString("luckycharm.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("luckycharm.icon").replace("{percentage}", CONFIG.getInt("luckycharm.percentage") + "%")),
                CONFIG.getInt("luckycharm.price"));
        this.mode = mode;

        this.percentage = CONFIG.getInt("luckycharm.percentage");

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
                evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("GOLDEN_APPLE : 1"));
            }
        }
    }

    @Override
    public int getMode() {
        return mode;
    }
}
