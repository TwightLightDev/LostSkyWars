package org.twightlight.skywars.listeners.skywars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillMessage;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.player.PlayerUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SkyWarsDeath implements Listener {
    @EventHandler
    public void onPlayerKill(SkyWarsPlayerDeathEvent e) {
        if (e.getKiller() == null) return;
        Account account = Database.getInstance().getAccount(e.getKiller().getUniqueId());
        if (account == null) {return;}

        int selectedKmId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.KILL_MESSAGE.getSelectionColumn());
        VisualCosmetic cos = VisualCosmetic.findByTypeAndId(VisualCosmeticType.KILL_MESSAGE, selectedKmId);
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

        int selectedKeId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.KILL_EFFECT.getSelectionColumn());
        VisualCosmetic cos1 = VisualCosmetic.findByTypeAndId(VisualCosmeticType.KILL_EFFECT, selectedKeId);
        if (cos1 instanceof SkyWarsKillEffect) {
            SkyWarsKillEffect cos2 = (SkyWarsKillEffect) cos1;
            cos2.execute(e.getKiller(), e.getPlayer(), e.getPlayer().getLocation());
        }
    }
}
