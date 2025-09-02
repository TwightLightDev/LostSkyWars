package org.twightlight.skywars.player;

import org.bukkit.Bukkit;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerCoinEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSoulEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerXpGainEvent;
import org.twightlight.skywars.arena.Arena;

import java.util.Arrays;

public class DataContainer {
    private int coinsEarned = 0;
    private double xpEarned = 0;
    private int soulsEarned = 0;
    private final Account account;
    private double gxpEarned = 0;

    public DataContainer(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public int getCoinsEarned() {
        return coinsEarned;
    }

    public void addCoins(int amount) {
        Arena<?> server = (Arena<?>) account.getServer();
        SkyWarsPlayerCoinEarnEvent e = new SkyWarsPlayerCoinEarnEvent(server, account.getPlayer(), amount);
        Bukkit.getPluginManager().callEvent(e);
        int final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addStat("coins", final_amount);
            this.coinsEarned += final_amount;
        }
    }

    public double getXpEarned() {
        return xpEarned;
    }

    public void addXp(double amount) {
        Arena<?> server = (Arena<?>) account.getServer();


        SkyWarsPlayerXpGainEvent e = new SkyWarsPlayerXpGainEvent(server, account.getPlayer(), amount);
        Bukkit.getPluginManager().callEvent(e);
        double final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addExp(final_amount);
            this.xpEarned += final_amount;
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

        Arena<?> server = (Arena<?>) account.getServer();
        SkyWarsPlayerSoulEarnEvent e = new SkyWarsPlayerSoulEarnEvent(server, account.getPlayer(), amount);
        Bukkit.getPluginManager().callEvent(e);
        int final_amount = e.getAmount();

        if (!server.isPrivate()) {
            account.addStat("souls", final_amount);
            this.soulsEarned += final_amount;
        }
    }
}
