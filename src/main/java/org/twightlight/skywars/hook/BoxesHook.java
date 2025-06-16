package org.twightlight.skywars.hook;

import io.github.losteddev.boxes.api.LostBoxesAPI;
import io.github.losteddev.boxes.api.box.RewardRarity;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.hook.boxes.BoxNPC;
import org.twightlight.skywars.hook.boxes.CosmeticReward;
import org.twightlight.skywars.utils.LostLogger;
import org.twightlight.skywars.utils.LostLogger.LostLevel;

public class BoxesHook {

    public static final LostLogger LOGGER = Main.LOGGER.getModule("BoxesHook");

    public static void setupBoxes() {
        LOGGER.log(LostLevel.INFO, "LostBoxes found, hooking...");

        for (Cosmetic cosmetic : CosmeticServer.SKYWARS.listCosmetics()) {
            if (cosmetic.getType() == CosmeticType.SKYWARS_SYMBOL) {
                continue;
            }
            if (cosmetic.getMode() == 3 && Language.options$ranked$freekitsandperks) {
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
        LOGGER.log(LostLevel.INFO, "LostBoxes found, destroying Vaults...");

        BoxNPC.listNPCs().forEach(BoxNPC::destroy);
    }
}
