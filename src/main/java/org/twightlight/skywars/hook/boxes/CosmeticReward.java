package org.twightlight.skywars.hook.boxes;

import io.github.losteddev.boxes.api.box.BoxReward;
import io.github.losteddev.boxes.api.box.RewardRarity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;

public class CosmeticReward extends BoxReward {

    private Cosmetic cosmetic;

    public CosmeticReward(Cosmetic cosmetic) {
        this.cosmetic = cosmetic;
    }

    @Override
    public String getId() {
        return "lsw[" + cosmetic.getType().ordinal() + "-" + cosmetic.getMode() + "-" + cosmetic.getId() + "]";
    }

    @Override
    public String getName() {
        String suffix = cosmetic.getType().getSize() > 1 ? "- " + SkyWarsType.fromIndex(cosmetic.getMode()).getName() : "";
        return cosmetic.getRawName() + " §7(" + cosmetic.getType().getName().substring(0, cosmetic.getType().getName().length() - (suffix.isEmpty() ? 1 : 0)) + suffix + ")";
    }

    @Override
    public RewardRarity getRarity() {
        return RewardRarity.fromIgnoreCase(cosmetic.getRarity().name().toLowerCase());
    }

    @Override
    public void give(Player player) {
        cosmetic.give(Database.getInstance().getAccount(player.getUniqueId()));
    }

    @Override
    public boolean has(Player player) {
        return cosmetic.has(Database.getInstance().getAccount(player.getUniqueId()));
    }
}
