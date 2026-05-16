package org.twightlight.skywars.integration.battlepass;

import io.github.battlepass.BattlePlugin;
import net.advancedplugins.bp.impl.actions.ActionRegistry;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;

public class BattlePassIntegration {
    private static ActionRegistry actionRegistry = BattlePlugin.getPlugin().getActionRegistry();
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("BattlePassHook");

    public static void setupBattlePass() {
        LOGGER.log(Logger.Level.INFO, "BattlePass found, hooking...");
        actionRegistry.quest(SkyWarsQuests::new);
    }

}
