package org.twightlight.skywars.hook.guilds.listeners;

import me.leoo.guilds.api.enums.LevelRewardEnum;
import me.leoo.guilds.bukkit.Guilds;
import me.leoo.guilds.bukkit.api.objects.Guild;
import me.leoo.guilds.bukkit.manager.GuildsManager;
import me.leoo.guilds.libs.utils.common.number.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent;
import org.twightlight.skywars.api.event.game.SkyWarsGameStartEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerCoinEarnEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerXpGainEvent;
import org.twightlight.skywars.player.CurrencyManager;
import org.twightlight.skywars.arena.Arena;

import java.util.*;

public class SkyWars implements Listener {
    private static final Map<Arena<?>, List<Player>> xp_multiplier = new HashMap<>();
    private static final Map<Arena<?>, List<Player>> coins_multiplier = new HashMap<>();

    @EventHandler
    public void onStart(SkyWarsGameStartEvent e) {
        if (e == null)
            return;
        if (e.getServer() instanceof Arena) {
            Arena<?> server = ((Arena<?>) e.getServer());
            for (Player player : server.getPlayers(false)) {
                Guild guild = GuildsManager.getByPlayer(player);
                if (guild == null)
                    return;
                if (NumberUtil.testChance(Integer.parseInt(guild.getLevel().getRewardValue(LevelRewardEnum.DOUBLE_XP)))) {
                    Objects.requireNonNull(player);
                    Guilds.get().getLanguage().getList("guilds.settings.boosters.double-xp.start-message").forEach(player::sendMessage);
                    xp_multiplier.computeIfAbsent(server, k -> new ArrayList<>()).add(player);
                    continue;
                }
                Optional.ofNullable(xp_multiplier.get(server)).ifPresent(list -> list.remove(player));

                if (NumberUtil.testChance(Integer.parseInt((guild.getLevel().getRewardValue(LevelRewardEnum.DOUBLE_COINS) != null) ? guild.getLevel().getRewardValue(LevelRewardEnum.DOUBLE_COINS) : "0"))) {
                    Objects.requireNonNull(player);
                    Guilds.get().getLanguage().getList("guilds.settings.boosters.double-coins.start-message").forEach(player::sendMessage);
                    coins_multiplier.computeIfAbsent(server, k -> new ArrayList<>()).add(player);
                    continue;
                }
                Optional.ofNullable(coins_multiplier.get(server)).ifPresent(list -> list.remove(player));
            }
        }
    }

    @EventHandler
    public void onEnd(SkyWarsGameEndEvent e) {
        if (e.getServer() instanceof Arena) {
            Arena<?> server = ((Arena<?>) e.getServer());
            xp_multiplier.remove(server);
        }
    }
    @EventHandler
    public void onXpGain(SkyWarsPlayerXpGainEvent e) {
        Player player = e.getPlayer();
        Guild guild = GuildsManager.getByPlayer(player);
        if (guild == null || !(e.getServer() instanceof Arena))
            return;
        Arena<?> server = ((Arena<?>) e.getServer());
        if (Optional.ofNullable(xp_multiplier.get(server)).isPresent() && xp_multiplier.get(server).contains(player) ) {
            e.setAmount(e.getAmount() * 2);
            guild.sendDoubleXpMessage(player);
        }
        e.addFinalTask(objects -> {
            CurrencyManager container = (CurrencyManager) objects.get(0);
            double amount = (double) objects.get(1);
            container.addGxp(amount * 0.6);
        });
    }

    @EventHandler
    public void onCoinEarn(SkyWarsPlayerCoinEarnEvent e) {
        Player player = e.getPlayer();
        Guild guild = GuildsManager.getByPlayer(player);
        if (guild == null || !(e.getServer() instanceof Arena))
            return;
        Arena<?> server = ((Arena<?>) e.getServer());
        if (Optional.ofNullable(coins_multiplier.get(server)).isPresent() && coins_multiplier.get(server).contains(player)) {
            e.setAmount(e.getAmount() * 2);
            List<String> var10000 = Guilds.get().getLanguage().getList("guilds.settings.boosters.double-coins.reward-message");
            Objects.requireNonNull(player);
            var10000.forEach(player::sendMessage);
        }
    }
}
