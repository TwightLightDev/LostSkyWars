package org.twightlight.skywars.player;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerCoinEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSoulEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerXpGainEvent;
import org.twightlight.skywars.arena.Arena;

import java.util.Arrays;

public class CurrencyManager {
    private int coinsEarned = 0;
    private double xpEarned = 0;
    private int soulsEarned = 0;
    private final Account account;
    private double gxpEarned = 0;

    public CurrencyManager(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public int getCoinsEarned() {
        return coinsEarned;
    }

    public void addCoins(int amount, SkyWarsPlayerCoinEarnEvent.CoinSource source) {
        Arena<?> server = (Arena<?>) account.getArena();
        SkyWarsPlayerCoinEarnEvent e = new SkyWarsPlayerCoinEarnEvent(server, account.getPlayer(), amount, source);
        Bukkit.getPluginManager().callEvent(e);
        int final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addStat("coins", final_amount);
            this.coinsEarned += final_amount;
            switch (source) {
                case PLAY:
                    account.getPlayer().sendMessage(Language.game$rewards_message$coins_per_play.
                            replace("{coins}", String.valueOf(final_amount)));
                    break;
                case KILL:
                    account.getPlayer().sendMessage(Language.game$rewards_message$coins_per_kill.
                            replace("{coins}", String.valueOf(final_amount)));
                    break;
                case WIN:
                    account.getPlayer().sendMessage(Language.game$rewards_message$coins_per_win.
                            replace("{coins}", String.valueOf(final_amount)));
                    break;
            }
        }
    }

    public double getXpEarned() {
        return xpEarned;
    }

    public void addXp(double amount, SkyWarsPlayerXpGainEvent.XpSource source) {
        Arena<?> server = (Arena<?>) account.getArena();


        SkyWarsPlayerXpGainEvent e = new SkyWarsPlayerXpGainEvent(server, account.getPlayer(), amount, source);
        Bukkit.getPluginManager().callEvent(e);
        double final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addExp(final_amount);
            this.xpEarned += final_amount;
            switch (source) {
                case PLAY:
                    account.getPlayer().sendMessage(Language.game$rewards_message$exp_per_play.
                            replace("{xp}", String.valueOf(final_amount)));
                    break;
                case KILL:
                    account.getPlayer().sendMessage(Language.game$rewards_message$exp_per_kill.
                            replace("{xp}", String.valueOf(final_amount)));
                    break;
                case WIN:
                    account.getPlayer().sendMessage(Language.game$rewards_message$exp_per_win.
                            replace("{xp}", String.valueOf(final_amount)));
                    break;
            }
        }

        e.getFinalTask().forEach(c -> c.accept(Arrays.asList(this, final_amount)));
    }

    public int getSoulsEarned() {
        return soulsEarned;
    }

    public void addGxp(double amount) {
        this.gxpEarned += amount;
    }

    public int getGxpEarned() {
        return (int) Math.round(gxpEarned);
    }

    public void addSouls(int amount) {

        Arena<?> server = (Arena<?>) account.getArena();
        SkyWarsPlayerSoulEarnEvent e = new SkyWarsPlayerSoulEarnEvent(server, account.getPlayer(), amount);
        Bukkit.getPluginManager().callEvent(e);
        int final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addStat("souls", final_amount);
            this.soulsEarned += final_amount;
            account.getPlayer().sendMessage(Language.game$rewards_message$soul.
                    replace("{xp}", String.valueOf(final_amount)));
        }
    }
}
