package org.twightlight.skywars.modules.boosters.boosters;

import org.bukkit.Bukkit;
import org.twightlight.skywars.utils.player.PlayerUtils;

import java.util.UUID;

public class BoosterData {

    private UUID owner;
    private String boosterID;
    private String ownerName;

    public BoosterData(UUID owner, String boosterID) {

        this.owner = owner;
        this.boosterID = boosterID;
        this.ownerName = PlayerUtils.replaceAll(Bukkit.getPlayer(owner), "{display}");
    }

    public String getBoosterID() {
        return boosterID;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName != null ? ownerName : "";
    }
}
