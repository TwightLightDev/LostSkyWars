package org.twightlight.skywars.hook.guilds.level;

import com.google.common.reflect.TypeToken;
import me.leoo.guilds.bukkit.manager.GuildsManager;
import org.bukkit.Bukkit;
import org.twightlight.skywars.hook.GuildsHook;
import org.twightlight.skywars.hook.guilds.donation.Donator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Level {
    private int currentXP;
    private int requiredXP;
    private int level;
    private Donator donator;

    public Level(Donator donator) {
        this.donator = donator;
        currentXP = GuildsHook.getExternalDB().getData(Bukkit.getPlayer(donator.getUUID()), "currentXP", new TypeToken<Integer>() {
        }, 0);
        level = GuildsHook.getExternalDB().getData(Bukkit.getPlayer(donator.getUUID()), "level", new TypeToken<Integer>() {
        }, 1);
        if (level == Integer.MAX_VALUE) {
            requiredXP = GuildsHook.getLevelConfig().getInt("level.max.requiredXP", 3000);
        } else {
            requiredXP = GuildsHook.getLevelConfig().getInt("level." + level + ".requiredXP", 3000);

        }
    }

    public void saveData() {
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(donator.getUUID()), currentXP, "currentXP");
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(donator.getUUID()), level, "level");
    }

    public double getCurrentXP() {
        return currentXP;
    }

    public double getRequiredXP() {
        return requiredXP;
    }

    public int getLevel() {
        return level;
    }

    public void addXP(int amount) {
        currentXP += amount;
        if (currentXP >= requiredXP) {
            levelUp();
        }
        GuildsManager.getByPlayer(Bukkit.getPlayer(donator.getUUID())).getLevel().addXp(amount);
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(donator.getUUID()), currentXP, "currentXP");

    }

    public void levelUp() {
        if (GuildsHook.getLevelConfig().getYml().contains("level." + (level + 1) + ".requiredXP")) {
            level += 1;
            requiredXP = GuildsHook.getLevelConfig().getInt("level." + level + ".requiredXP", 3000);
            double ratio = BigDecimal.valueOf(GuildsHook.getLevelConfig().getDouble("level." + level + ".ratio", 0.75D))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            donator.setRatio(ratio);
            donator.setDonationLimit(GuildsHook.getLevelConfig().getInt("level." + level + ".donation_limit", 1000));
            donator.saveData();
        } else {
            level = Integer.MAX_VALUE;
            requiredXP = GuildsHook.getLevelConfig().getInt("level.max.requiredXP", 3000);
            double ratio = BigDecimal.valueOf(GuildsHook.getLevelConfig().getDouble("level.max.ratio", 0.75D))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            donator.setRatio(ratio);
            donator.setDonationLimit(GuildsHook.getLevelConfig().getInt("level.max.donation_limit", 1000));
            donator.saveData();
        }
    }
}
