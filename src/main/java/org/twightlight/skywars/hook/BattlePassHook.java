package org.twightlight.skywars.hook;

import io.github.battlepass.BattlePlugin;
import net.advancedplugins.bp.impl.actions.ActionRegistry;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.battlepass.SkyWarsQuests;
import org.twightlight.skywars.utils.Logger;

public class BattlePassHook {
    private static ActionRegistry actionRegistry = BattlePlugin.getPlugin().getActionRegistry();
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("BattlePass Hook");

    public static void setupBattlePass() {
        LOGGER.log(Logger.Level.INFO, "BattlePass found, hooking...");
        actionRegistry.quest(SkyWarsQuests::new);
    }
}
