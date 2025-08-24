package org.twightlight.skywars.modules.boosters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerCoinEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSoulEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerXpGainEvent;
import org.twightlight.skywars.modules.boosters.User;
import org.twightlight.skywars.modules.boosters.boosters.Booster;

public class SkyWars implements Listener {
    @EventHandler
    public void onCoinEarn(SkyWarsPlayerCoinEarnEvent e) {
        User user = User.getFromUUID(e.getPlayer().getUniqueId());

        if (user == null || !user.hasBooster()) return;

        e.setAmount((int) (e.getAmount() * user.getTotalMultiplier(Booster.Currency.COINS)));
    }
    @EventHandler
    public void onSoulEarn(SkyWarsPlayerSoulEarnEvent e) {
        User user = User.getFromUUID(e.getPlayer().getUniqueId());

        if (user == null || !user.hasBooster()) return;

        e.setAmount((int) (e.getAmount() * user.getTotalMultiplier(Booster.Currency.SOULS)));
    }
    @EventHandler
    public void onExpGain(SkyWarsPlayerXpGainEvent e) {
        User user = User.getFromUUID(e.getPlayer().getUniqueId());

        if (user == null || !user.hasBooster()) return;

        e.setAmount((int) (e.getAmount() * user.getTotalMultiplier(Booster.Currency.EXP)));
    }
}
