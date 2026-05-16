package org.twightlight.skywars.integration.boxes;

import io.github.losteddev.boxes.api.box.BoxReward;
import io.github.losteddev.boxes.api.box.RewardRarity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.database.Database;

public class CosmeticReward extends BoxReward {

    private VisualCosmetic cosmetic;

    public CosmeticReward(VisualCosmetic cosmetic) {
        this.cosmetic = cosmetic;
    }

    @Override
    public String getId() {
        return "lsw[" + cosmetic.getVisualType().ordinal() + "-" + cosmetic.getId() + "]";
    }

    @Override
    public String getName() {
        return cosmetic.getRawName() + " §7(" + cosmetic.getVisualType().getDisplayName().trim() + ")";
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
