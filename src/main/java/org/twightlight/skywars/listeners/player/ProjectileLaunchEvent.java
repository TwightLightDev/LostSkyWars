package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsTrail;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

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
