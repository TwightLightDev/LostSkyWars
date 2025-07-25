package org.twightlight.skywars.player;

import org.twightlight.skywars.world.WorldServer;

public class DataContainer {
    private int coinsEarned = 0;
    private double xpEarned = 0;
    private int soulsEarned = 0;
    private final Account account;

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
        WorldServer<?> server = (WorldServer<?>) account.getServer();
        if (!server.isPrivate()) {
            account.addStat("coins", amount);
            this.coinsEarned += amount;
        }
    }

    public double getXpEarned() {
        return xpEarned;
    }

    public void addXp(double amount) {
        WorldServer<?> server = (WorldServer<?>) account.getServer();
        if (!server.isPrivate()) {
            account.addExp(amount);
            this.xpEarned += amount;
        }
    }

    public int getSoulsEarned() {
        return soulsEarned;
    }

    public void addSouls(int amount) {
        WorldServer<?> server = (WorldServer<?>) account.getServer();
        if (!server.isPrivate()) {
            account.addStat("souls", amount);
            this.soulsEarned += amount;
        }
    }
}
