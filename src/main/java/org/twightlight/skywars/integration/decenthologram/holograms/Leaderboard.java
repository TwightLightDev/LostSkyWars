package org.twightlight.skywars.integration.decenthologram.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.integration.decenthologram.DecentHologramsIntegration;
import org.twightlight.skywars.integration.decenthologram.User;

public class Leaderboard {

    private Hologram hologram;
    private Mode mode;

    public Leaderboard(String hologramId, Mode mode) {
        hologram = DHAPI.getHologram(hologramId);
        this.mode = mode;
        if (hologram != null) {
            hologram.setDefaultVisibleState(false);
            DecentHologramsIntegration.getLogger().log(Logger.Level.INFO, "Hologram id " + hologramId + " loaded!");
        }
        else {
            DecentHologramsIntegration.getLogger().log(Logger.Level.SEVERE, "Hologram id " + hologramId + " not exist. Please manually create it!");
        }

    }

    public Mode getMode() {
        return mode;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void hide(User user) {
        hologram.removeShowPlayer(user.getPlayer());
    }

    public void show(User user) {
        hologram.setShowPlayer(user.getPlayer());
    }

    public enum Mode {
        OVERALL,
        SOLO,
        TEAM,
        RANKED;

        public static Mode fromName(String name) {
            for (Mode mode : Mode.values()) {
                if (mode.name().equalsIgnoreCase(name)) {
                    return mode;
                }
            }

            return null;
        }
    }
}
