package org.twightlight.skywars.integration.boxes;

import io.github.losteddev.boxes.api.LostBoxesAPI;
import io.github.losteddev.boxes.api.box.RewardRarity;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;

public class BoxesIntegration {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("BoxesHook");

    public static void setupBoxes() {
        LOGGER.log(Level.INFO, "LostBoxes found, hooking...");

        for (VisualCosmetic cosmetic : VisualCosmetic.listAll()) {
            if (cosmetic.getVisualType() == VisualCosmeticType.SYMBOL) {
                continue;
            }

            RewardRarity rarity = RewardRarity.from(cosmetic.getRarity().name().toLowerCase());
            if (rarity != null) {
                LostBoxesAPI.addBoxReward(new CosmeticReward(cosmetic));
            }
        }

        BoxNPC.setupBoxNPCs();
    }

    public static void destroyBoxes() {
        LOGGER.log(Level.INFO, "LostBoxes found, destroying Vaults...");

        BoxNPC.listNPCs().forEach(BoxNPC::destroy);
    }
}
