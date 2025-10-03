package org.twightlight.skywars.modules.boosters.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerCoinEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSoulEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerXpGainEvent;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.boosters.api.event.BoosterMultiplyEvent;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.player.Account;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class SkyWars implements Listener {
    @EventHandler
    public void onCoinEarn(SkyWarsPlayerCoinEarnEvent e) {
        PlayerUser user = PlayerUser.getFromUUID(e.getPlayer().getUniqueId());
        ServerUser serverUser = ServerUser.getServerUser();

        if (user == null) return;
        if (!user.hasBooster() && !serverUser.hasBooster()) return;

        float p_multiplier = user.getTotalMultiplier(Booster.Currency.COINS);
        float s_multiplier = serverUser.getTotalMultiplier(Booster.Currency.COINS);

        int value = e.getAmount();
        BoosterMultiplyEvent e1 = new BoosterMultiplyEvent(
                Booster.Currency.COINS,
                user,
                (int) (value * ((p_multiplier - 1) + s_multiplier))
        );
        Bukkit.getPluginManager().callEvent(e1);
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            serverUser.getActivatingStream().getActivatingBooster().forEach(pair -> {
                Booster booster = BoosterManager.getBoosters().get(pair.getValue());
                if (booster == null) return;
                Database.getInstance().getAccountOffline(pair.getKey()).thenAccept((account) ->
                        {
                            int affiliateReward = (int) (value * (booster.getAmplifier() - 1) * booster.getAffiliateRate());
                            if (affiliateReward > 0) {
                                account.addStat("coins", affiliateReward);

                            }
                        }
                );
            });
        }
        e.setAmount((int) (value * (p_multiplier + s_multiplier)));
    }

    @EventHandler
    public void onSoulEarn(SkyWarsPlayerSoulEarnEvent e) {
        PlayerUser user = PlayerUser.getFromUUID(e.getPlayer().getUniqueId());
        ServerUser serverUser = ServerUser.getServerUser();

        if (user == null) return;
        if (!user.hasBooster() && !serverUser.hasBooster()) return;

        float p_multiplier = user.getTotalMultiplier(Booster.Currency.SOULS);
        float s_multiplier = serverUser.getTotalMultiplier(Booster.Currency.SOULS);

        int value = e.getAmount();
        BoosterMultiplyEvent e1 = new BoosterMultiplyEvent(
                Booster.Currency.SOULS,
                user,
                (int) (value * ((p_multiplier - 1) + s_multiplier))
        );
        Bukkit.getPluginManager().callEvent(e1);
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            serverUser.getActivatingStream().getActivatingBooster().forEach(pair -> {
                Booster booster = BoosterManager.getBoosters().get(pair.getValue());
                if (booster == null) return;
                Database.getInstance().getAccountOffline(pair.getKey()).thenAccept((account) ->
                        {
                            if (chanceOf((int) (booster.getAffiliateRate() * 100 * booster.getAmplifier()))) {
                                account.addStat("souls", value);

                            };
                        }
                );
            });
        }

        e.setAmount((int) (value * (p_multiplier + s_multiplier)));
    }

    @EventHandler
    public void onExpGain(SkyWarsPlayerXpGainEvent e) {
        PlayerUser user = PlayerUser.getFromUUID(e.getPlayer().getUniqueId());
        ServerUser serverUser = ServerUser.getServerUser();

        if (user == null) return;
        if (!user.hasBooster() && !serverUser.hasBooster()) return;

        float p_multiplier = user.getTotalMultiplier(Booster.Currency.EXP);
        float s_multiplier = serverUser.getTotalMultiplier(Booster.Currency.EXP);

        double value = e.getAmount();
        BoosterMultiplyEvent e1 = new BoosterMultiplyEvent(
                Booster.Currency.EXP,
                user,
                (int) (value * ((p_multiplier - 1) + s_multiplier))
        );
        Bukkit.getPluginManager().callEvent(e1);
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            serverUser.getActivatingStream().getActivatingBooster().forEach(pair -> {
                Booster booster = BoosterManager.getBoosters().get(pair.getValue());
                if (booster == null) return;
                Database.getInstance().getAccountOffline(pair.getKey()).thenAccept((account) ->
                        {
                            double affiliateReward = value * (booster.getAmplifier() - 1) * booster.getAffiliateRate();
                            if (affiliateReward > 0) {
                                account.addExp(affiliateReward);

                            }
                            ;
                        }
                );
            });
        }
        e.setAmount(value * (p_multiplier + s_multiplier));
    }

    private static final Random RANDOM = new Random();

    public static boolean chanceOf(int percent) {
        if (percent <= 0) return false;
        if (percent >= 100) return true;
        return RANDOM.nextInt(100) < percent;
    }
}
