package org.twightlight.skywars.integration.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import org.bukkit.Location;

public class WEHelper {

    public void removeRegion(Location center, int radius, int height) {
        com.sk89q.worldedit.world.World weWorld = BukkitUtil.getLocalWorld(center.getWorld());

        Vector min = new Vector(
                center.getBlockX() - radius,
                center.getBlockY(),
                center.getBlockZ() - radius
        );
        Vector max = new Vector(
                center.getBlockX() + radius,
                center.getBlockY() + height,
                center.getBlockZ() + radius
        );

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                .getEditSession(weWorld, -1); // -1 = unlimited blocks

        try {
            editSession.setBlocks(
                    new com.sk89q.worldedit.regions.CuboidRegion(weWorld, min, max),
                    new BaseBlock(0)
            );
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        } finally {
            editSession.flushQueue();
        }
    }
}
