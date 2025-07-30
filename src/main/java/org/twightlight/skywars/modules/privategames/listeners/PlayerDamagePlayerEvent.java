package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.world.WorldServer;

public class PlayerDamagePlayerEvent implements Listener {
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player attacker = (Player) e.getDamager();

        Account account = Database.getInstance().getAccount(attacker.getUniqueId());
        if (account == null) return;

        if (!(account.getServer() instanceof WorldServer<?>)) return;
        WorldServer<?> server = (WorldServer<?>) account.getServer();

        if (server.isPrivate()) {
            PrivateGamesUser owner = server.getServerOwner();
            if (owner.getInstantKillSetting().getValue() == 1) {
                ((Player) e.getEntity()).damage(Integer.MAX_VALUE, attacker);
            }
        }
    }
}
