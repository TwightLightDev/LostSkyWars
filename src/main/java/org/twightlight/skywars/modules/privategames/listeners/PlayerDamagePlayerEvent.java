package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.player.Account;

public class PlayerDamagePlayerEvent implements Listener {
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player attacker = (Player) e.getDamager();

        Account account = Database.getInstance().getAccount(attacker.getUniqueId());
        if (account == null) return;

        if (!(account.getServer() instanceof Arena<?>)) return;
        Arena<?> server = (Arena<?>) account.getServer();

        if (server.isPrivate()) {
            User owner = server.getServerOwner();
            if (owner.getInstantKillSetting().getValue() == 1) {
                ((Player) e.getEntity()).damage(Integer.MAX_VALUE, attacker);
            }
        }
    }
}
