package org.twightlight.skywars.modules.lobbysettings;

import com.google.common.reflect.TypeToken;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.player.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User implements ModulesUser {
    private UUID uuid;

    private static Map<UUID, User> userList = new HashMap<>();

    private boolean fly;
    private int speed;
    private int jumpboost;
    private boolean vanish;
    private boolean showScoreboard;
    private boolean showParticles;
    private boolean showChat;

    public User(Player p) {
        uuid = p.getUniqueId();
        userList.put(uuid, this);
        fly =  Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "fly", new TypeToken<String>() {
        }, "false"));
        speed =  LobbySettings.getDatabase().getData(p, "speed", new TypeToken<Integer>() {
        }, 0);
        jumpboost =  LobbySettings.getDatabase().getData(p, "jumpboost", new TypeToken<Integer>() {
        }, 0);
        vanish =  Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "vanish", new TypeToken<String>() {
        }, "false"));
        showScoreboard = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showScoreboard", new TypeToken<String>() {
        }, "true"));
        showParticles = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showParticles", new TypeToken<String>() {
        }, "true"));
        showChat = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showChat", new TypeToken<String>() {
        }, "true"));
    }
    public boolean isChatVisible() {
        return showChat;
    }

    public void setChatVisibility(boolean showChat, boolean modify) {
        this.showChat = showChat;
        if (modify) {
            LobbySettings.getDatabase().updateData(Bukkit.getPlayer(uuid), String.valueOf(showChat), "showChat");
        }
    }

    public boolean isParticlesVisible() {
        return showParticles;
    }

    public void setParticlesVisibility(boolean showParticles, boolean modify) {
        this.showParticles = showParticles;
        if (modify) {
            LobbySettings.getDatabase().updateData(Bukkit.getPlayer(uuid), String.valueOf(showParticles), "showParticles");
        }
    }

    public boolean isScoreboardVisible() {
        return showScoreboard;
    }

    public void setScoreboardVisibility(boolean showScoreboard, boolean modify) {
        Account account = Database.getInstance().getAccount(uuid);
        if (account == null) return;
        if (showScoreboard) {
            account.reloadScoreboard();
        } else {
            account.getScoreboard().hide();
        }

        this.showScoreboard = showScoreboard;
        if (modify) {
            LobbySettings.getDatabase().updateData(Bukkit.getPlayer(uuid), String.valueOf(showScoreboard), "showScoreboard");
        }
    }

    public boolean isFlyEnable() {
        return fly;
    }

    public void setFlyEnable(boolean fly, boolean modify) {
        this.fly = fly;

        Bukkit.getPlayer(uuid).setAllowFlight(fly);
        if (modify) {
            LobbySettings.getDatabase().updateData(Bukkit.getPlayer(uuid), String.valueOf(fly), "fly");
        }
    }

    public boolean isVanish() {
        return vanish;
    }

    private boolean isVanish(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public void setVanishState(boolean vanish, boolean modify) {
        this.vanish = vanish;

        Player p = Bukkit.getPlayer(uuid);
        if (vanish) {
            if (!isVanish(p)) {
                VanishAPI.getPlugin().visibilityChanger.hidePlayer(p);
            }
        } else {
            if (isVanish(p)) {
                VanishAPI.getPlugin().visibilityChanger.showPlayer(p);
            }
        }
        if (modify) {
            LobbySettings.getDatabase().updateData(p, String.valueOf(vanish), "vanish");
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed, boolean modify) {
        this.speed = speed;
        Player player = Bukkit.getPlayer(uuid);
        player.removePotionEffect(PotionEffectType.SPEED);
        if (speed >= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed - 1, false, false));
        }
        if (modify) {
            LobbySettings.getDatabase().updateData(player, speed, "speed");
        }
    }

    public int getJumpBoost() {
        return jumpboost;
    }

    public void setJumpBoost(int jumpboost, boolean modify) {
        this.jumpboost = jumpboost;
        Player player = Bukkit.getPlayer(uuid);

        player.removePotionEffect(PotionEffectType.JUMP);
        if (jumpboost >= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, jumpboost - 1, false, false));
        }
        if (modify) {
            LobbySettings.getDatabase().updateData(player, jumpboost, "jumpboost");
        }
    }

    public void enable() {
        Player p = Bukkit.getPlayer(uuid);
        boolean fly =  Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "fly", new TypeToken<String>() {
        }, "false"));
        int speed =  LobbySettings.getDatabase().getData(p, "speed", new TypeToken<Integer>() {
        }, 0);
        int jumpboost =  LobbySettings.getDatabase().getData(p, "jumpboost", new TypeToken<Integer>() {
        }, 0);
        boolean vanish =  Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "vanish", new TypeToken<String>() {
        }, "false"));
        boolean showScoreboard = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showScoreboard", new TypeToken<String>() {
        }, "true"));
        boolean showParticles = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showParticles", new TypeToken<String>() {
        }, "true"));
        boolean showChat = Boolean.parseBoolean(LobbySettings.getDatabase().getData(p, "showChat", new TypeToken<String>() {
        }, "true"));

        setScoreboardVisibility(showScoreboard, false);
        setVanishState(vanish, false);
        setSpeed(speed, false);
        setJumpBoost(jumpboost, false);
        setFlyEnable(fly, false);
        setParticlesVisibility(showParticles, false);
        setChatVisibility(showChat, false);
    }

    public void disable() {
        setScoreboardVisibility(true, false);
        setFlyEnable(false, false);
        setJumpBoost(0, false);
        setSpeed(0, false);
        setVanishState(false, false);
        setParticlesVisibility(true, false);
        setChatVisibility(true, false);

    }

    public UUID getUUID() {
        return uuid;
    }

    public void sendMessage(String msg) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

    }

    public void sendMessage(List<String> msgs) {
        Player p = Bukkit.getPlayer(uuid);
        msgs.forEach(line -> {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        });
    }
    public static User getFromUUID(UUID uuid) {
        return userList.get(uuid);
    }

    public static User removeUser(User user) {
        return userList.remove(user.uuid);
    }

    public static Map<UUID, User> getUsers() {
        return userList;
    }
}
