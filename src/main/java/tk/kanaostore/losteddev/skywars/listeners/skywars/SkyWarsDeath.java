package tk.kanaostore.losteddev.skywars.listeners.skywars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.visual_cosmetics.SkyWarsKillMessage;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.PlayerUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SkyWarsDeath implements Listener {
    @EventHandler
    public void onPlayerKill(SkyWarsPlayerDeathEvent e) {
        Account account = Database.getInstance().getAccount(e.getKiller().getUniqueId());
        if (account == null) {return;}
        Cosmetic cos = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLMESSAGE, 1);
        if (cos instanceof SkyWarsKillMessage) {
            SkyWarsKillMessage cos1 = (SkyWarsKillMessage) cos;
            SkyWarsPlayerDeathEvent.SkyWarsDeathCause cause = e.getCause();
            String msg;
            List<String> msgs;
            switch (cause) {
                case KILLED_MOB:
                    msgs = cos1.getMobMessage();
                    if (!msgs.isEmpty()) {
                        msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
                    } else {
                        msg = e.getKillMessage();
                    }
                    e.setKillMessage(PlayerUtils.replaceAll(e.getPlayer(), e.getKiller() ,msg));
                    break;
                case KILLED_BOW:
                    msgs = cos1.getBowMessage();
                    if (!msgs.isEmpty()) {
                        msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
                    } else {
                        msg = e.getKillMessage();
                    }
                    e.setKillMessage(PlayerUtils.replaceAll(e.getPlayer(), e.getKiller() ,msg));
                    break;
                case KILLED_MELEE:
                    msgs = cos1.getMeleeMessage();
                    if (!msgs.isEmpty()) {
                        msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
                    } else {
                        msg = e.getKillMessage();
                    }
                    e.setKillMessage(PlayerUtils.replaceAll(e.getPlayer(), e.getKiller() ,msg));
                    break;
                case KILLED_VOID:
                    msgs = cos1.getVoidMessage();
                    if (!msgs.isEmpty()) {
                        msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
                    } else {
                        msg = e.getKillMessage();
                    }
                    e.setKillMessage(PlayerUtils.replaceAll(e.getPlayer(), e.getKiller() ,msg));
                    break;
                default:
                    msg = e.getKillMessage();
                    e.setKillMessage(msg);
                    break;
            }
        }

    }
}
