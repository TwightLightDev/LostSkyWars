package org.twightlight.skywars.listeners.skywars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillMessage;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.PlayerUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SkyWarsDeath implements Listener {
    @EventHandler
    public void onPlayerKill(SkyWarsPlayerDeathEvent e) {
        if (e.getKiller() == null) return;
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
        Cosmetic cos1 = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLEFFECT, 1);
        if (cos1 instanceof SkyWarsKillEffect) {
            SkyWarsKillEffect cos2 = (SkyWarsKillEffect) cos1;
            cos2.execute(e.getKiller(), e.getPlayer(), e.getPlayer().getLocation());
        }
    }
}
