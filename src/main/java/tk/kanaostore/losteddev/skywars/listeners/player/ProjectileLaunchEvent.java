package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.visual_cosmetics.SkyWarsTrail;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;

public class ProjectileLaunchEvent implements Listener {
    @EventHandler
    public void onProjectileLaunch(org.bukkit.event.entity.ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity().getShooter();

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
            return;
        }

        Cosmetic cos = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_TRAIL, 1);
        if (cos instanceof SkyWarsTrail) {
            SkyWarsTrail trail = (SkyWarsTrail) cos;
            trail.getConsumer().accept(e);
        }
    }
}
