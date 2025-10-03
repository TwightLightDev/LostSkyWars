package org.twightlight.skywars.arena.ui.chest;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.Holograms;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.arena.ui.enums.SkyWarsEvent;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;
import org.twightlight.skywars.utils.*;
import org.twightlight.skywars.arena.Arena;

import java.text.SimpleDateFormat;

public class SkyWarsChest {

    private Arena<?> server;
    private String serialized;
    private String chestType;

    private int fillCount;
    private Hologram hologram = null;

    public SkyWarsChest(Arena<?> server, String serialized) {
        this.server = server;
        this.serialized = serialized;
        this.chestType = serialized.split("; ")[6];
        fillCount = 0;
    }

    public void update() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram != null) {
            Block block = this.getLocation().getBlock();
            if (!(block.getState() instanceof Chest)) {
                this.destroy();
                return;
            }

            NMS.playChestAction(this.getLocation(), true);
            if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Refill) {
                this.hologram.updateLine(1, Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
            } else if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Doom) {
                this.hologram.updateLine(1, Language.game$hologram$no_refill);
            }
        }
    }

    public void createHologram() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram == null) {
            this.hologram = Holograms.createHologram(this.getLocation().add(0.5, -0.5, 0.5));
            if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Refill) {
                this.hologram.withLine(Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
            } else if (server.getTimeline().get(server.getEventTime(true)) == SkyWarsEvent.Doom) {
                this.hologram.withLine(Language.game$hologram$no_refill);
            }
        }
    }

    public void destroy() {
        if (this.server.getType().equals(SkyWarsType.DUELS)) {
            return;
        }

        if (this.hologram != null) {
            NMS.playChestAction(this.getLocation(), false);
            Holograms.removeHologram(this.hologram);
            this.hologram = null;
        }
    }

    public void setType(ChestType chestType) {
        this.chestType = chestType.getName();
    }

    public void fill() {
        ChestType type = ChestType.getByName(chestType);
        if (type == null) {
            type = ChestType.getFirst();
        }

        if (type != null) {
            type.fill(getLocation(), fillCount);
        }
        fillCount += 1;
    }

    public String getChestType() {
        return chestType;
    }

    public Location getLocation() {
        return BukkitUtils.deserializeLocation(serialized, server);
    }

    @Override
    public String toString() {
        return BukkitUtils.serializeLocation(getLocation()) + "; " + chestType;
    }

    public void reset() {
        fillCount = 0;

    }
}
